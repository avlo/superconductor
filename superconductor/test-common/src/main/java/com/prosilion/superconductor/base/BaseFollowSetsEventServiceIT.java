package com.prosilion.superconductor.base;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.BadgeSetsEvent;
import com.prosilion.nostr.event.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.SetsPairedEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseFollowSetsEventServiceIT extends BaseIntegrationTestFixtures {
  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventService;

  private final EventServiceIF eventServiceIF;

  protected final BadgeSetsEvent badgeSetsUpvoteEvent;

  public BaseFollowSetsEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheFollowSetsEventService") CacheFollowSetsEventServiceIF cacheFollowSetsEventService) throws ParseException {
    super(superconductorInstanceIdentity);
    this.eventServiceIF = eventServiceIF;
    this.cacheFollowSetsEventService = cacheFollowSetsEventService;

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);
    cacheServiceIF.save(awardUpvoteDefinitionEvent);

    FormulaEvent plusOneFormulaEvent = new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
    cacheServiceIF.save(plusOneFormulaEvent);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       parameterAimgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       relay,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       plusOneFormulaEvent);
    cacheServiceIF.save(badgeDefinitionReputationEventPlusOneFormula);

    AddressTag badgeDefnEventAsAddressTag = badgeAwardUpvoteEvent.getBadgeDefinitionEvent().asAddressableEventAddressTag();

    SetsPairedEvent setsPairedEvents = new SetsPairedEvent(
       badgeDefnEventAsAddressTag,
       new EventTag(badgeAwardUpvoteEvent.getId(), badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElse(null)));

    CuratedBadgeAwardGenericEvent curationSetsUpvoteEvent = new CuratedBadgeAwardGenericEvent(
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
    Util.debug(log, "test setup db events:\n{}",
       cacheServiceIF.getAll().stream().map(GenericEventRecord::createPrettyPrintJson).collect(Collectors.joining(",\n")),
       true, '1');
  }

  protected final BadgeSetsEvent getBadgeSetsUpvoteEvent() {
    return badgeSetsUpvoteEvent;
  }

  @Test
  public void testSaveBadgeAwardReputationEventUpvote() {
    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
       parameterAimgIdentity,
       badgeSetsUpvoteEvent,
       relay);

    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent), followSetsEvent.getRelay().orElseThrow());

    FollowSetsEvent dbFollowSetsEventByEventId = cacheFollowSetsEventService.getEvent(followSetsEvent.getId(), relay).orElseThrow();
    assertEquals(followSetsEvent, dbFollowSetsEventByEventId);

    assertEquals(dbFollowSetsEventByEventId.getAwardRecipientPublicKey(), recipient.getPublicKey());
    assertTrue(
       dbFollowSetsEventByEventId.getBadgeSetsEventList().stream()
          .map(BadgeSetsEvent::getBadgeDefinitionReputationEvent).anyMatch(badgeDefinitionReputationEventPlusOneFormula::equals));

    assertEquals(followSetsEvent.getBadgeSetsEventList(), dbFollowSetsEventByEventId.getBadgeSetsEventList());
    assertEquals(followSetsEvent.getEventTags(), dbFollowSetsEventByEventId.getEventTags());
    assertEquals(followSetsEvent.asAddressableEventAddressTag(), dbFollowSetsEventByEventId.asAddressableEventAddressTag());
    assertEquals(followSetsEvent.getIdentifierTag(), dbFollowSetsEventByEventId.getIdentifierTag());
    assertEquals(followSetsEvent.getAwardRecipientPublicKey(), dbFollowSetsEventByEventId.getAwardRecipientPublicKey());
    assertEquals(followSetsEvent.getBadgeSetsEventList().size(), dbFollowSetsEventByEventId.getBadgeSetsEventList().size());
    assertEquals(1, followSetsEvent.getBadgeSetsEventList().size());

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService()
          .send(
             new ReqMessage(
                Factory.generateRandomHex64String(),
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
  }
}
