package com.prosilion.superconductor.supplier;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestDirtiesContextFixtures;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static com.prosilion.nostr.util.Util.generateRandomHex64String;
import static com.prosilion.superconductor.BaseCacheFollowSetsEventServiceIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseCacheCuratedBadgeDefinitionEventMessageListIT extends BaseIntegrationTestDirtiesContextFixtures {
  protected final String definitionEventRelayUrl;
  protected final Relay definitionEventRelay;

  private final List<BadgeDefinitionGenericEvent> badgeDefinitionGenericEventList;

  abstract protected List<BadgeDefinitionGenericEvent> createBadgeDefinitionGenericEventList();

  protected AbstractBaseCacheCuratedBadgeDefinitionEventMessageListIT(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorInstanceIdentity);
    this.definitionEventRelayUrl = superconductorRelayUrl;
    this.definitionEventRelay = new Relay(superconductorRelayUrl);

    this.badgeDefinitionGenericEventList = createBadgeDefinitionGenericEventList();
    setupBadgeDefinitionEvents(badgeDefinitionGenericEventList);
  }

  private void setupBadgeDefinitionEvents(List<BadgeDefinitionGenericEvent> badgeDefinitionGenericEventList) {
    Set<String> eventIds = new HashSet<>();
    badgeDefinitionGenericEventList.forEach(badgeDefinitionGenericEvent -> {
      NostrEventPublisher definitionEventNostrEventPublisher = new NostrEventPublisher(definitionEventRelayUrl);
      EventMessage eventMessageBadgeDefinitionUpvoteEventWithRelayTag = new EventMessage(badgeDefinitionGenericEvent);
      assertTrue(
         definitionEventNostrEventPublisher
            .send(
               eventMessageBadgeDefinitionUpvoteEventWithRelayTag)
            .getFlag());

      List<EventIF> returnedEventIFs = getEventIFs(
         new NostrSingleRequestService().send(
            new ReqMessage(
               generateRandomHex64String(),
               new Filters(
                  new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
            definitionEventRelayUrl));

      log.debug("returned events:");
      log.debug("  {}", returnedEventIFs);

      eventIds.addAll(returnedEventIFs.stream()
         .map(event -> event.requireFirstTag(EventTag.class))
         .map(EventTag::getEventId).collect(Collectors.toSet()));
    });

    assertTrue(eventIds.stream().anyMatch(this.badgeDefinitionGenericEventList.stream().map(BaseEvent::getId).toList()::contains));
  }

  @Test
  void testExpectedEvent() throws NostrException {
    List<EventIF> returnedCuratedBadgeDefinitionEvents = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));
    log.debug("returned events:");
    log.debug("  {}", returnedCuratedBadgeDefinitionEvents);

    assertEquals(badgeDefinitionGenericEventList.size(), returnedCuratedBadgeDefinitionEvents.size());

    List<String> eventIds = returnedCuratedBadgeDefinitionEvents.stream()
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();

    assertTrue(badgeDefinitionGenericEventList.stream().map(BadgeDefinitionGenericEvent::getId).toList().containsAll(eventIds));

    assertTrue(returnedCuratedBadgeDefinitionEvents.stream()
       .map(event -> event.requireFirstTag(AddressTag.class))
       .allMatch(badgeDefinitionGenericEventList.stream().map(BadgeDefinitionGenericEvent::asAddressableEventAddressTag).toList()::contains));
  }
}
