package com.prosilion.superconductor;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheFollowSetsEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
   "superconductor.event.curation.active=true"
})
public class FollowSetsEventServiceRxRIT extends BaseIntegrationTestFixtures {
  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventService;
  private final CacheServiceIF cacheServiceIF;

  private final EventServiceIF eventServiceIF;
  private final Relay relay;
  private final BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;

  @Autowired
  public FollowSetsEventServiceRxRIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheFollowSetsEventService") CacheFollowSetsEventService cacheFollowSetsEventService) {
    super(superconductorInstanceIdentity);
    this.relay = new Relay(relayUrl);
    this.eventServiceIF = eventServiceIF;
    this.cacheFollowSetsEventService = cacheFollowSetsEventService;
    this.cacheServiceIF = cacheServiceIF;

    this.awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);
    cacheServiceIF.save(awardUpvoteDefinitionEvent);

    CuratedFormulaEvent plusOneCuratedFormulaEvent = new CuratedFormulaEvent(aImgIdentity,
       new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA, relay),
       new ReferenceTag(relayUrl),
       relay);
    cacheServiceIF.save(plusOneCuratedFormulaEvent);

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       parameterAimgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent);
    cacheServiceIF.save(badgeDefinitionReputationEventPlusOneFormula);

//    SetsPairedEvent setsPairedEvents = new SetsPairedEvent(
//       badgeDefnEventAsAddressTag,
//       new EventTag(badgeAwardUpvoteEvent.getId(), badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElse(null)));

    Util.debug(log, "test setup db events:\n{}",
       cacheServiceIF.getAll().stream().map(GenericEventRecord::createPrettyPrintJson).collect(Collectors.joining(",\n")),
       true, '1');
  }

  @Test
  void testGetByPubKeyTag() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent_1 = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    CuratedBadgeAwardGenericEvent curatedBadgeAwardUpvoteEvent_1 = new CuratedBadgeAwardGenericEvent(
       parameterAimgIdentity,
       badgeAwardUpvoteEvent_1,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent_1.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    cacheServiceIF.save(curatedBadgeAwardUpvoteEvent_1);

    BadgeSetsEvent badgeSetsUpvoteEvent_1 = new BadgeSetsEvent(
       parameterAimgIdentity,
       badgeDefinitionReputationEventPlusOneFormula,
       curatedBadgeAwardUpvoteEvent_1,
       relay);
    cacheServiceIF.save(badgeSetsUpvoteEvent_1);

    FollowSetsEvent followSetsEvent_1 = new FollowSetsEvent(
       parameterAimgIdentity,
       badgeSetsUpvoteEvent_1,
       relay);
    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent_1), followSetsEvent_1.getRelay().orElseThrow());

    List<FollowSetsEvent> actual_1 = cacheFollowSetsEventService.getBy(new PubKeyTag(recipient.getPublicKey()));
    assertEquals(1, actual_1.size());
//
// start event 2
//
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent_2 = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    CuratedBadgeAwardGenericEvent curatedBadgeAwardUpvoteEvent_2 = new CuratedBadgeAwardGenericEvent(
       parameterAimgIdentity,
       badgeAwardUpvoteEvent_2,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent_2.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    cacheServiceIF.save(curatedBadgeAwardUpvoteEvent_2);
//    eventServiceIF.processIncomingEvent(new EventMessage(curationSetsUpvoteEvent_2), curationSetsUpvoteEvent_2.getRelay().orElseThrow());

    BadgeSetsEvent badgeSetsUpvoteEvent_2 = new BadgeSetsEvent(
       parameterAimgIdentity,
       badgeDefinitionReputationEventPlusOneFormula,
       List.of(curatedBadgeAwardUpvoteEvent_1, curatedBadgeAwardUpvoteEvent_2),
       relay);
//    cacheServiceIF.deleteEvent(badgeSetsUpvoteEvent_1);

//    *************************************************
//    *************************************************
//    must delete existing badgeSetsEvent
//    *************************************************
//    *************************************************    
    DeletionEvent deletionBadgeSetsUpvoteEvent_1 = deleteFxn(badgeSetsUpvoteEvent_1);
    eventServiceIF.processIncomingEvent(new EventMessage(deletionBadgeSetsUpvoteEvent_1), badgeAwardUpvoteEvent_1.getRelay().orElseThrow());
    cacheServiceIF.save(badgeSetsUpvoteEvent_2);

    FollowSetsEvent followSetsEvent_2 = new FollowSetsEvent(
       parameterAimgIdentity,
       badgeSetsUpvoteEvent_2,
       relay);

//    cacheServiceIF.deleteEvent(followSetsEvent_1);

//    *************************************************
//    *************************************************
//    must delete existing followsetsEvent
//    *************************************************
//    *************************************************    
    DeletionEvent deletionFollowSetsEvent_1 = deleteFxn(followSetsEvent_1);
    eventServiceIF.processIncomingEvent(new EventMessage(deletionFollowSetsEvent_1), followSetsEvent_1.getRelay().orElseThrow());

    cacheServiceIF.save(badgeSetsUpvoteEvent_2);
    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent_2), followSetsEvent_2.getRelay().orElseThrow());
    List<FollowSetsEvent> actual_2 = cacheFollowSetsEventService.getBy(new PubKeyTag(recipient.getPublicKey()));
    assertEquals(1, actual_2.size());

    assertTrue(cacheServiceIF.getEventByEventId(followSetsEvent_2.getId()).isPresent());

//    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent), followSetsEvent.getRelay().orElseThrow());
//
//    FollowSetsEvent dbFollowSetsEventByEventId = cacheFollowSetsEventService.getEvent(followSetsEvent.getId(), relay).orElseThrow();
//    assertEquals(followSetsEvent, dbFollowSetsEventByEventId);
//
//    assertEquals(dbFollowSetsEventByEventId.getAwardRecipientPublicKey(), recipient.getPublicKey());
//    assertTrue(
//       dbFollowSetsEventByEventId.getBadgeSetsEventList().stream()
//          .map(BadgeSetsEvent::getBadgeDefinitionReputationEvent).anyMatch(badgeDefinitionReputationEventPlusOneFormula::equals));
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
//    assertTrue(returnedEventIFs.stream().map(EventIF::getKind).toList().contains(Kind.FOLLOW_SETS));
//
//    assertTrue(returnedEventIFs.stream().map(cacheFollowSetsEventService::materialize)
//       .flatMap(Optional::stream)
//       .anyMatch(dbFollowSetsEventByEventId::equals));
  }

  private DeletionEvent deleteFxn(EventIF eventIF) {
    return new DeletionEvent(aImgIdentity, List.of(new EventTag(eventIF.getId())), "delete me");
  }
}
