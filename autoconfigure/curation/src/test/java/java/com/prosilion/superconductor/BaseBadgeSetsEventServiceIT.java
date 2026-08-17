package java.com.prosilion.superconductor;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.SetsPairedEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
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
public abstract class BaseBadgeSetsEventServiceIT extends BaseIntegrationTestFixtures {
  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;
  private final CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;

  private final CuratedBadgeAwardGenericEvent curationSetsUpvoteEvent;
  private final BadgeSetsEvent badgeSetsUpvoteEvent;

  public BaseBadgeSetsEventServiceIT(
     @NonNull @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("cacheBadgeSetsEventService") CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF) {
    super(superconductorInstanceIdentity);
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
    Relay relay = new Relay(relayUrl);

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       BaseIntegrationTestFixtures.upvoteDefnCreator, BaseIntegrationTestFixtures.upvoteIdentifierTag, relay);
    cacheServiceIF.save(awardUpvoteDefinitionEvent);

    CuratedFormulaEvent plusOneCuratedFormulaEvent = new CuratedFormulaEvent(BaseIntegrationTestFixtures.aImgIdentity,
       new FormulaEvent(BaseIntegrationTestFixtures.formulaCreator, BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag, awardUpvoteDefinitionEvent, BaseIntegrationTestFixtures.PLUS_ONE_FORMULA, relay),
       new ReferenceTag(relayUrl),
       relay);

    cacheServiceIF.save(plusOneCuratedFormulaEvent);

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       parameterAimgIdentity,
       BaseIntegrationTestFixtures.repDefnCreator.getPublicKey(),
       BaseIntegrationTestFixtures.reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent);
    cacheServiceIF.save(badgeDefinitionReputationEventPlusOneFormula);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
       BaseIntegrationTestFixtures.submitter,
       BaseIntegrationTestFixtures.recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    SetsPairedEvent setsPairedEvents = new SetsPairedEvent(
       awardUpvoteDefinitionEvent.asAddressableEventAddressTag(),
       new EventTag(badgeAwardUpvoteEvent.getId(), badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()));

    this.curationSetsUpvoteEvent = new CuratedBadgeAwardGenericEvent(
       parameterAimgIdentity,
       badgeAwardUpvoteEvent,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    cacheServiceIF.save(curationSetsUpvoteEvent);

    this.badgeSetsUpvoteEvent = new BadgeSetsEvent(
       parameterAimgIdentity,
       badgeDefinitionReputationEventPlusOneFormula,
       curationSetsUpvoteEvent,
       relay);
    cacheServiceIF.save(badgeSetsUpvoteEvent);
  }

  @Test
  public void testGetByEventId() {
    Optional<BadgeSetsEvent> byEventId = cacheBadgeSetsEventServiceIF
       .getEvent(
          badgeSetsUpvoteEvent.getId(),
          badgeSetsUpvoteEvent.getRelay().orElseThrow());
    assertTrue(byEventId.isPresent());
  }

  @Test
  public void testGetByPubKeyTagEventTag() {
    Optional<BadgeSetsEvent> byAddressTag = cacheBadgeSetsEventServiceIF.getBy(
       new PubKeyTag(BaseIntegrationTestFixtures.recipient.getPublicKey()),
       new EventTag(curationSetsUpvoteEvent.getEventId(), curationSetsUpvoteEvent.getRelay().orElseThrow().getUrl()));
    assertTrue(byAddressTag.isPresent());
    assertEquals(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
    compareEvents(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
  }

  @Test
  public void testGetByPubKeyTag() {
    List<BadgeSetsEvent> byPubKeyTag = cacheBadgeSetsEventServiceIF.getBy(new PubKeyTag(BaseIntegrationTestFixtures.recipient.getPublicKey()));
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
       new PubKeyTag(BaseIntegrationTestFixtures.recipient.getPublicKey()),
       badgeDefinitionReputationEventPlusOneFormula.asAddressableEventAddressTag());
    assertTrue(byAddressTag.isPresent());
    assertEquals(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
    compareEvents(badgeSetsUpvoteEvent, byAddressTag.orElseThrow());
  }

  @Test
  public void testGetByPubKeyTagIdentifierTag() {
    Optional<BadgeSetsEvent> byAddressTag = cacheBadgeSetsEventServiceIF.getBy(
       new PubKeyTag(BaseIntegrationTestFixtures.recipient.getPublicKey()),
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
