package com.prosilion.superconductor.base;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.SetsPairedEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheCuratedBadgeAwardEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
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
public abstract class BaseCacheCuratedBadgeAwardGenericEventServiceIT {
  public static final String REPUTATION = "TEST_REPUTATION";
  public static final String AWARD_UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
  public static final String FORMULA_UNIT_UPVOTE = "FORMULA_UNIT_UPVOTE";
  public static final String FORMULA_UNIT_DOWNVOTE = "FORMULA_UNIT_DOWNVOTE";

  public static final String PLUS_ONE_FORMULA = "+1";

  protected final IdentifierTag reputationIdentifierTag = new IdentifierTag(REPUTATION);
  protected final IdentifierTag upvoteIdentifierTag = new IdentifierTag(AWARD_UNIT_UPVOTE);
  protected final IdentifierTag formulaUpvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_UPVOTE);

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
  private final CacheCuratedBadgeAwardEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;

  private final Relay relay;

  Duration requestTimeoutDuration;
  CuratedBadgeAwardGenericEvent curationSetsUpvoteEvent;

  public BaseCacheCuratedBadgeAwardGenericEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("cacheCurationSetsEventService") CacheCuratedBadgeAwardEventServiceIF cacheCuratedBadgeAwardEventServiceIF,
     Duration requestTimeoutDuration) throws ParseException {
    this.aImgIdentity = superconductorInstanceIdentity;
    this.requestTimeoutDuration = requestTimeoutDuration;
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardEventServiceIF;
    this.relay = new Relay(relayUrl);

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);

    FormulaEvent plusOneFormulaEvent = new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);

    this.badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       relay,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       plusOneFormulaEvent);

    AddressTag badgeDefnEventAsAddressTag = badgeAwardUpvoteEvent.getBadgeDefinitionEvent().asAddressableEventAddressTag();

    SetsPairedEvent setsPairedEvents = new SetsPairedEvent(
       badgeDefnEventAsAddressTag,
       new EventTag(badgeAwardUpvoteEvent.getId(), badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElse(null)));

    this.curationSetsUpvoteEvent = new CuratedBadgeAwardGenericEvent(
       aImgIdentity,
       badgeAwardUpvoteEvent,
       relay);
    cacheServiceIF.save(curationSetsUpvoteEvent);
  }

  @Test
  public void testGetEventByPubKeyTag() {
    List<CuratedBadgeAwardGenericEvent> byPubKeyTag = cacheCuratedBadgeAwardGenericEventServiceIF
       .getBy(
          new PubKeyTag(curationSetsUpvoteEvent.getAwardRecipientPublicKey()));
    assertEquals(1, byPubKeyTag.size());
    assertEquals(curationSetsUpvoteEvent, byPubKeyTag.getFirst());
  }

  @Test
  public void testGetEventByPubKeyTagEventTag() {
    Optional<CuratedBadgeAwardGenericEvent> byPubKeyTagEventTag = cacheCuratedBadgeAwardGenericEventServiceIF
       .getBy(
          new PubKeyTag(curationSetsUpvoteEvent.getAwardRecipientPublicKey()),
          curationSetsUpvoteEvent.getEventTag());
    assertTrue(byPubKeyTagEventTag.isPresent());
  }

  @Test
  public void testGetEventByPubKeyTagIdentifierTag() {
    List<CuratedBadgeAwardGenericEvent> byPubKeyTagIdentifierTag = cacheCuratedBadgeAwardGenericEventServiceIF
       .getBy(new PubKeyTag(curationSetsUpvoteEvent.getAwardRecipientPublicKey()), curationSetsUpvoteEvent.getIdentifierTag());
    assertEquals(1, byPubKeyTagIdentifierTag.size());
    assertEquals(curationSetsUpvoteEvent, byPubKeyTagIdentifierTag.getFirst());
  }

  @Test
  public void testGetEventByEventTag() {
    Optional<CuratedBadgeAwardGenericEvent> byPubKeyTagIdentifierTag = cacheCuratedBadgeAwardGenericEventServiceIF
       .getByDirect(curationSetsUpvoteEvent.getEventTag());
    assertTrue(byPubKeyTagIdentifierTag.isPresent());
    assertEquals(curationSetsUpvoteEvent, byPubKeyTagIdentifierTag.get());
  }

  @Test
  public void testGetEventByEventIdRelay() {
    String eventId = curationSetsUpvoteEvent.getEventId();
    Optional<CuratedBadgeAwardGenericEvent> byEventIdRelay = cacheCuratedBadgeAwardGenericEventServiceIF
       .getEvent(eventId, relay);
    assertTrue(byEventIdRelay.isPresent());
    assertEquals(curationSetsUpvoteEvent, byEventIdRelay.get());

    Optional<CuratedBadgeAwardGenericEvent> byEventIdNonExistentRelay = cacheCuratedBadgeAwardGenericEventServiceIF
       .getEvent(eventId, new Relay("ws://localhost-non-existent:5555"));
    assertTrue(byEventIdNonExistentRelay.isPresent());
    assertEquals(curationSetsUpvoteEvent, byEventIdNonExistentRelay.get());
  }

  @Test
  public void testThrowsException() {
    String nonExistentEventId = Util.generateRandomHex64String();
    assertEquals(Optional.empty(), cacheCuratedBadgeAwardGenericEventServiceIF.getEvent(nonExistentEventId, relay));

    EventTag nonExistentEventTagEventId = new EventTag(nonExistentEventId);
    assertEquals(Optional.empty(), cacheCuratedBadgeAwardGenericEventServiceIF.getByDirect(nonExistentEventTagEventId));
  }

//  @Test
//  public void testSaveBadgeAwardReputationEventUpvote() {
//    cacheCurationSetsEventServiceIF.getEvent(curationSetsUpvoteEvent.getEventId());
//
//    FollowSetsEvent dbFollowSetsEventByEventId = cacheFollowSetsEventService.getEvent(followSetsEvent.getId(), relay).orElseThrow();
//    assertEquals(followSetsEvent, dbFollowSetsEventByEventId);
//
//    List<String> badgeAwardAbstractEvents = dbFollowSetsEventByEventId.getBadgeSetsEventList().stream()
//       .map(BadgeSetsEvent::getCurationSetsEventList).flatMap(Collection::stream)
//       .map(curationSetsEvent ->
//          curationSetsEvent.getEventTag().getEventId()).toList();
//
//    assertTrue(badgeAwardAbstractEvents.contains(badgeAwardUpvoteEvent.getId()));
//
//    PublicKey matchPubkey = dbFollowSetsEventByEventId.getAwardRecipientPublicKey();
//    assertEquals(matchPubkey, recipient.getPublicKey());
//
//    assertEquals(followSetsEvent.getBadgeSetsEventList(), dbFollowSetsEventByEventId.getBadgeSetsEventList());
//    assertEquals(followSetsEvent.getEventTags(), dbFollowSetsEventByEventId.getEventTags());
//    assertEquals(followSetsEvent.asAddressableEventAddressTag(), dbFollowSetsEventByEventId.asAddressableEventAddressTag());
//    assertEquals(followSetsEvent.getIdentifierTag(), dbFollowSetsEventByEventId.getIdentifierTag());
//    assertEquals(followSetsEvent.getAwardRecipientPublicKey(), dbFollowSetsEventByEventId.getAwardRecipientPublicKey());
//    assertEquals(followSetsEvent.getBadgeSetsEventList().size(), dbFollowSetsEventByEventId.getBadgeSetsEventList().size());
//    assertEquals(1, followSetsEvent.getBadgeSetsEventList().size());
//
//    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
//       new NostrSingleRequestService()
//          .send(
//             new ReqMessage(
//                Factory.generateRandomHex64String(),
//                new Filters(
//                   new KindFilter(
//                      Kind.FOLLOW_SETS))),
//             relay.getUrl()));
//
//    log.debug("returned events:");
//    log.debug("  {}", returnedEventIFs);
//
//    assertTrue(returnedEventIFs.stream().map(EventIF::getKind).toList().contains(Kind.FOLLOW_SETS));
//
//    assertTrue(dbFollowSetsEventByEventId.getBadgeSetsEventList().stream().map(BadgeSetsEvent::getBadgeDefinitionReputationEvent)
//       .map(BadgeDefinitionReputationEvent::getFormulaEvents)
//       .anyMatch(badgeDefinitionReputationEventPlusOneFormula::equals));
//  }
}
