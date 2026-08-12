package com.prosilion.superconductor.base.curated.supplier.local;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.curated.supplier.AbstractBaseCacheCuratedBadgeAwardEventMessageListIT;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractCacheCuratedBadgeAwardEventMessageSupplierLocalListIT extends AbstractBaseCacheCuratedBadgeAwardEventMessageListIT {
  protected AbstractCacheCuratedBadgeAwardEventMessageSupplierLocalListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String relayUrl) throws NostrException {
    super(superconductorInstanceIdentity, relayUrl, relayUrl);
  }

  @Override
  protected List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> createBadgeAwardEventList() {
    return List.of(
       createAwardEventWithRelayTagDefinitionEventWithRelayTag(),
       createAwardEventWithoutRelayTagDefinitionEventWithoutRelayTag(),
       createAwardEventWithRelayTagDefinitionEventWithoutRelayTag(),
       createAwardEventWithoutRelayTagDefinitionEventWithRelayTag());
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithRelayTagDefinitionEventWithRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          new Relay(definitionEventRelayUrl)),
       new Relay(awardEventRelayUrl));
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTagDefinitionEventWithRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          new Relay(definitionEventRelayUrl)));
  }


  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithRelayTagDefinitionEventWithoutRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag),
       new Relay(awardEventRelayUrl));
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTagDefinitionEventWithoutRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          downvoteIdentifierTag));
  }

  protected void validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardUpvoteEvents) {
    List<EventIF> sanityCheckReturnedBadgeDefinitionEvents = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned BadgeDefinitionEvents:");
    log.debug("  {}", sanityCheckReturnedBadgeDefinitionEvents);

    Set<String> sanityCheckCurationSetsBadgeDefinitionEventIds = sanityCheckReturnedBadgeDefinitionEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).collect(Collectors.toSet());

    assertTrue(sanityCheckCurationSetsBadgeDefinitionEventIds.stream().anyMatch(
       badgeAwardGenericEventList.stream().map(BadgeAwardAbstractEvent::getBadgeDefinitionEvent)
          .map(BaseEvent::getId).toList()::contains));

    badgeAwardUpvoteEvents.forEach(badgeAwardUpvoteEvent ->
       assertTrue(
          new NostrEventPublisher(awardEventRelayUrl).send(
             new EventMessage(badgeAwardUpvoteEvent), Duration.ofSeconds(5)).getFlag()));
  }
}
