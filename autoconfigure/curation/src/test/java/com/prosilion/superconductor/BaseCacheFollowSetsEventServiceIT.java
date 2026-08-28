package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.BadgeSetsEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.BaseIntegrationTestDirtiesContextFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.DeleteEventServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import static com.prosilion.nostr.util.Util.generateRandomHex64String;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseCacheFollowSetsEventServiceIT extends BaseIntegrationTestDirtiesContextFixtures {
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventService;
  private final BadgeSetsEventKindPlugin badgeSetsEventKindPlugin;

  protected final EventServiceIF eventServiceIF;
  protected final Relay relay;

  protected final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;
  protected final BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;
  protected final CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent_1;
  protected final BadgeSetsEvent dbSynchedBadgeSetsEvent_1;

  protected final CacheServiceIF cacheServiceIF;
  protected final DeleteEventServiceIF deleteEventServiceIF;

  public BaseCacheFollowSetsEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull BadgeSetsEventKindPlugin badgeSetsEventKindPlugin,
     @NonNull DeleteEventServiceIF deleteEventServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheFollowSetsEventService") CacheFollowSetsEventServiceIF cacheFollowSetsEventService) {
    super(superconductorInstanceIdentity);
    this.badgeSetsEventKindPlugin = badgeSetsEventKindPlugin;
    this.cacheServiceIF = cacheServiceIF;
    this.deleteEventServiceIF = deleteEventServiceIF;
    this.relay = new Relay(relayUrl);
    this.eventServiceIF = eventServiceIF;
    this.cacheFollowSetsEventService = cacheFollowSetsEventService;

    this.awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);
    cacheServiceIF.save(awardUpvoteDefinitionEvent);

    CuratedFormulaEvent plusOneCuratedFormulaEvent = new CuratedFormulaEvent(aImgIdentity,
       new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA, relay),
       new ReferenceTag(relayUrl),
       relay);
    cacheServiceIF.save(plusOneCuratedFormulaEvent);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent_1 = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       superconductorInstanceIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent);
    cacheServiceIF.save(badgeDefinitionReputationEventPlusOneFormula);

    this.curatedBadgeAwardGenericEvent_1 = new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardUpvoteEvent_1,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent_1.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    cacheServiceIF.save(curatedBadgeAwardGenericEvent_1);

    BadgeSetsEvent setupTempGerCtorBadgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationEventPlusOneFormula,
       curatedBadgeAwardGenericEvent_1,
       relay);

    GenericEventRecord dbSynchedBadgeSetsEventGER = badgeSetsEventKindPlugin.processIncomingEvent(setupTempGerCtorBadgeSetsEvent, relay).orElseThrow();

    this.dbSynchedBadgeSetsEvent_1 = new BadgeSetsEvent(
       dbSynchedBadgeSetsEventGER,
       badgeDefinitionReputationEventPlusOneFormula,
       List.of(curatedBadgeAwardGenericEvent_1));

    Util.debug(log, "test setup db events:\n{}",
       cacheServiceIF.getAll().stream().map(GenericEventRecord::createPrettyPrintJson).collect(Collectors.joining(",\n")),
       true, '1');
  }

  protected final BadgeSetsEvent getBadgeSetsUpvoteEvent() {
    return dbSynchedBadgeSetsEvent_1;
  }

  @Test
  public void testSimulateIncomingBadgeAwardUpvoteEvent() {
    FollowSetsEvent followSetsEvent_1 = new FollowSetsEvent(
       superconductorInstanceIdentity,
       dbSynchedBadgeSetsEvent_1,
       relay);

    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent_1), followSetsEvent_1.getRelay().orElseThrow());
    validateDbUpdatedFollowSetsEvent(followSetsEvent_1);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent_2 = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent_2 = new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardUpvoteEvent_2,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent_2.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    cacheServiceIF.save(curatedBadgeAwardGenericEvent_2);

    BadgeSetsEvent setupTempGerCtorBadgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationEventPlusOneFormula,
       curatedBadgeAwardGenericEvent_2,
       relay);

//    cacheServiceIF.save(badgeSetsUpvoteEvent_2);
//    GenericEventRecord dbSynchedBadgeSetsEventGER = badgeSetsEventKindPlugin.processIncomingEvent(setupTempGerCtorBadgeSetsEvent, relay).orElseThrow();

    BadgeSetsEvent dbSynchedBadgeSetsEvent_2 = new BadgeSetsEvent(
       setupTempGerCtorBadgeSetsEvent.asGenericEventRecord(),
       badgeDefinitionReputationEventPlusOneFormula,
       List.of(
          curatedBadgeAwardGenericEvent_1,
          curatedBadgeAwardGenericEvent_2));

    FollowSetsEvent followSetsEvent_2 = new FollowSetsEvent(
       superconductorInstanceIdentity,
       dbSynchedBadgeSetsEvent_2,
       relay);

    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent_2), followSetsEvent_2.getRelay().orElseThrow());
    validateDbUpdatedFollowSetsEvent(followSetsEvent_2);
  }

  private void validateDbUpdatedFollowSetsEvent(FollowSetsEvent followSetsEvent) {
    FollowSetsEvent dbFollowSetsEventByEventId = cacheFollowSetsEventService.getBy(new PubKeyTag(followSetsEvent.getAwardRecipientPublicKey())).getFirst();

    assertEquals(dbFollowSetsEventByEventId.getAwardRecipientPublicKey(), recipient.getPublicKey());
    assertTrue(
       dbFollowSetsEventByEventId.getBadgeSetsEventList().stream()
          .map(BadgeSetsEvent::getBadgeDefinitionReputationEvent).anyMatch(badgeDefinitionReputationEventPlusOneFormula::equals));

    List<CuratedBadgeAwardGenericEvent> badgeSetsEventList_1 = followSetsEvent.getBadgeSetsEventList().stream().map(BadgeSetsEvent::getCuratedBadgeAwardGenericEventList).flatMap(Collection::stream).toList();
    List<CuratedBadgeAwardGenericEvent> badgeSetsEventList_2 = dbFollowSetsEventByEventId.getBadgeSetsEventList().stream().map(BadgeSetsEvent::getCuratedBadgeAwardGenericEventList).flatMap(Collection::stream).toList();
    assertTrue(badgeSetsEventList_1.containsAll(badgeSetsEventList_2));
//    assertEquals(followSetsEvent.getEventTags(), dbFollowSetsEventByEventId.getEventTags());
    assertEquals(followSetsEvent.asAddressableEventAddressTag(), dbFollowSetsEventByEventId.asAddressableEventAddressTag());
    assertEquals(followSetsEvent.getIdentifierTag(), dbFollowSetsEventByEventId.getIdentifierTag());
    assertEquals(followSetsEvent.getAwardRecipientPublicKey(), dbFollowSetsEventByEventId.getAwardRecipientPublicKey());
    assertEquals(followSetsEvent.getBadgeSetsEventList().size(), dbFollowSetsEventByEventId.getBadgeSetsEventList().size());
    assertEquals(1, followSetsEvent.getBadgeSetsEventList().size());

    List<EventIF> returnedEventIFs = getEventIFs(
       new NostrSingleRequestService()
          .send(
             new ReqMessage(
                generateRandomHex64String(),
                new Filters(
                   new KindFilter(
                      Kind.FOLLOW_SETS))),
             relay.getUrl()));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);
    assertTrue(returnedEventIFs.stream().map(EventIF::getKind).toList().contains(Kind.FOLLOW_SETS));

    assertTrue(returnedEventIFs.stream().map(cacheFollowSetsEventService::materialize)
       .flatMap(Optional::stream)
       .anyMatch(dbFollowSetsEventByEventId::equals));
    log.debug("done");
  }

  public static List<EventIF> getEventIFs(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
       .filter(EventMessage.class::isInstance)
       .map(EventMessage.class::cast)
       .map(EventMessage::getEvent)
       .toList();
  }
}
