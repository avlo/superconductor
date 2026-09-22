package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheFollowSetsEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestDirtiesContextFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.Comparator;
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
public class FollowSetsEventServiceRxRIT extends BaseIntegrationTestDirtiesContextFixtures {
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
       superconductorInstanceIdentity,
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
    BadgeAwardCanonicalEvent badgeAwardUpvoteEvent_1 = new BadgeAwardCanonicalEvent(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    CuratedBadgeAwardCanonicalEvent curatedBadgeAwardUpvoteEvent_1 = new CuratedBadgeAwardCanonicalEvent(
       superconductorInstanceIdentity,
       badgeAwardUpvoteEvent_1,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent_1.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    cacheServiceIF.save(curatedBadgeAwardUpvoteEvent_1);

    BadgeSetsEvent badgeSetsEvent_1 = getBadgeSetsEvent(List.of(curatedBadgeAwardUpvoteEvent_1));

    FollowSetsEvent followSetsEvent_1 = new FollowSetsEvent(
       superconductorInstanceIdentity,
       badgeSetsEvent_1,
       relay);
    
    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent_1), followSetsEvent_1.getRelay().orElseThrow());
    
    List<FollowSetsEvent> actual_1 = cacheFollowSetsEventService.getBy(new PubKeyTag(recipient.getPublicKey()));
    assertEquals(1, actual_1.size());
    assertTrue(cacheServiceIF.getEventByEventId(actual_1.getFirst().getId()).isPresent());

//
// start event 2
//
    BadgeAwardCanonicalEvent badgeAwardUpvoteEvent_2 = new BadgeAwardCanonicalEvent(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    CuratedBadgeAwardCanonicalEvent curatedBadgeAwardUpvoteEvent_2 = new CuratedBadgeAwardCanonicalEvent(
       superconductorInstanceIdentity,
       badgeAwardUpvoteEvent_2,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent_2.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    cacheServiceIF.save(curatedBadgeAwardUpvoteEvent_2);

    BadgeSetsEvent badgeSetsEvent_2 = getBadgeSetsEvent(
       List.of(
          curatedBadgeAwardUpvoteEvent_1,
          curatedBadgeAwardUpvoteEvent_2));

    FollowSetsEvent followSetsEvent_2 = new FollowSetsEvent(
       superconductorInstanceIdentity,
       badgeSetsEvent_2,
       relay);

    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent_2), relay);
    List<FollowSetsEvent> actual_2 = cacheFollowSetsEventService.getBy(new PubKeyTag(recipient.getPublicKey()));
    assertEquals(1, actual_2.size());

    assertTrue(cacheServiceIF.getEventByEventId(actual_2.getFirst().getId()).isPresent());
  }

  private @NonNull BadgeSetsEvent getBadgeSetsEvent(List<CuratedBadgeAwardCanonicalEvent> curatedBadgeAwardCanonicalEventList) {
    BadgeSetsEvent setupTempGerCtorBadgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationEventPlusOneFormula,
       curatedBadgeAwardCanonicalEventList,
       relay);

    eventServiceIF.processIncomingEvent(new EventMessage(setupTempGerCtorBadgeSetsEvent), relay);
    GenericEventRecord newestBadgeSetsEvent = cacheServiceIF.getByKind(Kind.BADGE_SETS_EVENT).stream().max(Comparator.comparing(GenericEventRecord::getCreatedAt)).orElseThrow();

    BadgeSetsEvent badgeSetsEvent = new BadgeSetsEvent(
       newestBadgeSetsEvent,
       badgeDefinitionReputationEventPlusOneFormula,
       curatedBadgeAwardCanonicalEventList);
    return badgeSetsEvent;
  }
}
