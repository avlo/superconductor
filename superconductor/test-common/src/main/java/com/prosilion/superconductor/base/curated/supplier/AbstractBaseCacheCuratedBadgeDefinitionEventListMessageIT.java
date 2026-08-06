package com.prosilion.superconductor.base.curated.supplier;

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
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseCacheCuratedBadgeDefinitionEventListMessageIT extends BaseIntegrationTestFixtures {
  protected final String definitionEventRelayUrl;
  protected final Relay definitionEventRelay;

  private final List<BadgeDefinitionGenericEvent> badgeDefinitionGenericEvents;

  abstract protected List<BadgeDefinitionGenericEvent> createDefinitionEvents();

  protected AbstractBaseCacheCuratedBadgeDefinitionEventListMessageIT(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorInstanceIdentity);
    this.definitionEventRelayUrl = superconductorRelayUrl;
    this.definitionEventRelay = new Relay(superconductorRelayUrl);

    this.badgeDefinitionGenericEvents = createDefinitionEvents();
    setupBadgeDefinitionEvents(badgeDefinitionGenericEvents);
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

      List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
         new NostrSingleRequestService().send(
            new ReqMessage(
               Factory.generateRandomHex64String(),
               new Filters(
                  new KindFilter(Kind.BADGE_DEFINITION_EVENT))),
            definitionEventRelayUrl));

      log.debug("returned events:");
      log.debug("  {}", returnedEventIFs);

      eventIds.addAll(returnedEventIFs.stream().map(EventIF::getId).collect(Collectors.toSet()));
    });

    assertEquals(this.badgeDefinitionGenericEvents.size(), eventIds.size());
    assertTrue(eventIds.stream().anyMatch(badgeDefinitionGenericEvents.stream().map(BaseEvent::getId).toList()::contains));
  }

  @Test
  void testExpectedEvent() throws NostrException {
    List<EventIF> returnedCuratedBadgeDefinitionEvents = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedCuratedBadgeDefinitionEvents);

    List<String> eventIds = returnedCuratedBadgeDefinitionEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();

    assertTrue(eventIds.stream().anyMatch(badgeDefinitionGenericEvents.stream().map(BadgeDefinitionGenericEvent::getId).toList()::contains));

    assertTrue(returnedCuratedBadgeDefinitionEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(AddressTag.class))
       .anyMatch(badgeDefinitionGenericEvents.stream().map(BadgeDefinitionGenericEvent::asAddressableEventAddressTag).toList()::contains));
  }
}
