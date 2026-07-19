package com.prosilion.superconductor.base;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.BadgeSetsEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.CuratedBadgeAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.SetsPairedEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseBadgeSetsEventServiceIT {
  public static final String REPUTATION = "TEST_REPUTATION";
  public static final String AWARD_UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
  public static final String FORMULA_UNIT_UPVOTE = "FORMULA_UNIT_UPVOTE";
  public static final String FORMULA_UNIT_DOWNVOTE = "FORMULA_UNIT_DOWNVOTE";

  public static final String PLUS_ONE_FORMULA = "+1";

  protected final IdentifierTag reputationIdentifierTag = new IdentifierTag(REPUTATION);
  protected final IdentifierTag upvoteIdentifierTag = new IdentifierTag(AWARD_UNIT_UPVOTE);
  protected final IdentifierTag formulaUpvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_UPVOTE);
  protected final IdentifierTag formulaDownvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_DOWNVOTE);

  private final Identity aImgIdentity;

  protected final Identity submitter =
//     Identity.generateRandomIdentity();
     Identity.create("aaa4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity upvoteDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("bbb4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity recipient =
//     Identity.generateRandomIdentity();
     Identity.create("ccc4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity formulaCreator =
//     Identity.generateRandomIdentity();
     Identity.create("ddd4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity repDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("eee4585483196998204846989544737603523651520600328805626488477202");

  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;
  private final BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent;
  private final CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;

  private final Relay relay;
  private final CacheServiceIF cacheServiceIF;

  Duration requestTimeoutDuration;

  private final CuratedBadgeAwardEvent curationSetsUpvoteEvent;
  BadgeSetsEvent badgeSetsUpvoteEvent;

  public BaseBadgeSetsEventServiceIT(
     @NonNull @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheBadgeSetsEventService") CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF,
     Duration requestTimeoutDuration) throws ParseException {
    this.cacheServiceIF = cacheServiceIF;
    this.aImgIdentity = superconductorInstanceIdentity;
    this.requestTimeoutDuration = requestTimeoutDuration;
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
    this.relay = new Relay(relayUrl);

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);
    cacheServiceIF.save(awardUpvoteDefinitionEvent);

    FormulaEvent plusOneFormulaEvent = new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
    cacheServiceIF.save(plusOneFormulaEvent);

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       relay,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       plusOneFormulaEvent);
    cacheServiceIF.save(badgeDefinitionReputationEventPlusOneFormula);

    this.badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    SetsPairedEvent setsPairedEvents = new SetsPairedEvent(
       awardUpvoteDefinitionEvent.asAddressableEventAddressTag(),
       new EventTag(badgeAwardUpvoteEvent.getId(), badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()));

    this.curationSetsUpvoteEvent = new CuratedBadgeAwardEvent(
       aImgIdentity,
       badgeAwardUpvoteEvent,
       relay);
    cacheServiceIF.save(curationSetsUpvoteEvent);

    this.badgeSetsUpvoteEvent = new BadgeSetsEvent(
       aImgIdentity,
       badgeDefinitionReputationEventPlusOneFormula,
       curationSetsUpvoteEvent,
       relay);
    cacheServiceIF.save(badgeSetsUpvoteEvent);
  }

  @Test
  public void testGetByEventId() {
    Optional<BadgeSetsEvent> byPubKeyTag = cacheBadgeSetsEventServiceIF
       .getEvent(
          badgeSetsUpvoteEvent.getId(),
          badgeSetsUpvoteEvent.getRelay().orElseThrow());
    assertTrue(byPubKeyTag.isPresent());
  }

  @Test
  public void testGetByPubKeyTagEventTag() {
    Optional<BadgeSetsEvent> byAddressTag = cacheBadgeSetsEventServiceIF.getBy(
       new PubKeyTag(recipient.getPublicKey()),
       new EventTag(curationSetsUpvoteEvent.getEventId(), curationSetsUpvoteEvent.getRelay().orElseThrow().getUrl()));
    assertTrue(byAddressTag.isPresent());
    assertEquals(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
    compareEvents(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
  }

  @Test
  public void testGetByPubKeyTag() {
    List<BadgeSetsEvent> byPubKeyTag = cacheBadgeSetsEventServiceIF.getBy(new PubKeyTag(recipient.getPublicKey()));
    assertEquals(1, byPubKeyTag.size());
    BadgeSetsEvent first = byPubKeyTag.getFirst();
    assertEquals(badgeSetsUpvoteEvent, first);
    compareEvents(badgeSetsUpvoteEvent, first);
  }

  @Test
  public void testGetByAddressTag() {
    Optional<BadgeSetsEvent> byAddressTag = cacheBadgeSetsEventServiceIF.getByDirect(
       badgeDefinitionReputationEventPlusOneFormula.asAddressableEventAddressTag());
    assertTrue(byAddressTag.isPresent());
    assertEquals(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
    compareEvents(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
  }

  @Test
  public void testGetByPubKeyTagAddressTag() {
    Optional<BadgeSetsEvent> byAddressTag = cacheBadgeSetsEventServiceIF.getBy(
       new PubKeyTag(recipient.getPublicKey()),
       badgeDefinitionReputationEventPlusOneFormula.asAddressableEventAddressTag());
    assertTrue(byAddressTag.isPresent());
    assertEquals(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
    compareEvents(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
  }

  @Test
  public void testGetByPubKeyTagIdentifierTag() {
    Optional<BadgeSetsEvent> byAddressTag = cacheBadgeSetsEventServiceIF.getBy(
       new PubKeyTag(recipient.getPublicKey()),
       new IdentifierTag(
          badgeDefinitionReputationEventPlusOneFormula.getReputationDefinitionCreatorPublicKey().toHexString()));
    assertTrue(byAddressTag.isPresent());
    assertEquals(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
    compareEvents(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
  }

  public void compareEvents(BaseEvent baseEvent1, BaseEvent baseEvent2) {
    assertEquals(baseEvent1.getId(), baseEvent2.getId());
    assertEquals(baseEvent1.getPublicKey(), baseEvent2.getPublicKey());
    assertEquals(baseEvent1.getKind(), baseEvent2.getKind());
    assertEquals(baseEvent1.getContent(), baseEvent2.getContent());
    assertEquals(baseEvent1.getSignature(), baseEvent2.getSignature());
  }
}
