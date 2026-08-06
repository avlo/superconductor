package com.prosilion.superconductor.base.curated.supplier;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.time.Duration;
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
public abstract class AbstractBaseCacheCuratedBadgeAwardEventListMessageIT extends BaseIntegrationTestFixtures {
  protected final Identity superconductorInstanceIdentity;

  protected final String awardEventRelayUrl;
  protected final String definitionEventRelayUrl;

  protected final List<BadgeDefinitionGenericEvent> badgeDefinitionGenericEvents;
  protected final List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardGenericEvents;

  abstract protected List<BadgeDefinitionGenericEvent> createBadgeDefinitionEvents();
  abstract protected List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> createBadgeAwardEvents();

  protected AbstractBaseCacheCuratedBadgeAwardEventListMessageIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) throws NostrException {
    super(superconductorInstanceIdentity);

    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.definitionEventRelayUrl = definitionEventRelayUrl;
    this.awardEventRelayUrl = awardEventRelayUrl;

    this.badgeDefinitionGenericEvents = createBadgeDefinitionEvents();
    setupBadgeDefinitionEvents(badgeDefinitionGenericEvents, definitionEventRelayUrl);

    this.badgeAwardGenericEvents = createBadgeAwardEvents();
    setupBadgeAwardEvents(badgeAwardGenericEvents);
  }

  private void setupBadgeDefinitionEvents(List<BadgeDefinitionGenericEvent> badgeDefinitionGenericEventList, String definitionEventRelayUrl) {
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
                  new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
            definitionEventRelayUrl));

      log.debug("returned events:");
      log.debug("  {}", returnedEventIFs);

      eventIds.addAll(returnedEventIFs.stream().map(EventIF::asGenericEventRecord)
         .map(event -> event.requireFirstTag(EventTag.class))
         .map(EventTag::getEventId).collect(Collectors.toSet()));
    });

    assertEquals(this.badgeDefinitionGenericEvents.size(), eventIds.size());
    assertTrue(eventIds.stream().anyMatch(badgeDefinitionGenericEvents.stream().map(BaseEvent::getId).toList()::contains));
  }

  private void setupBadgeAwardEvents(List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardUpvoteEvents) {
    badgeAwardUpvoteEvents.forEach(badgeAwardUpvoteEvent -> {
      NostrEventPublisher awardEventNostrComprehensiveClient = new NostrEventPublisher(awardEventRelayUrl);
      EventMessage eventMessageBadgeAwardUpvoteEvent = new EventMessage(badgeAwardUpvoteEvent);
      assertTrue(
         awardEventNostrComprehensiveClient
            .send(
               eventMessageBadgeAwardUpvoteEvent, Duration.ofSeconds(10))
            .getFlag());
    });
  }

  @Test
  void testExpectedEventViaGeneralRequest() throws NostrException {
    List<EventIF> returnedCuratedBadgeAwardEvents = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_AWARD_EVENT))),
          awardEventRelayUrl,
          Duration.ofSeconds(10)));

    log.debug("returned events:");
    log.debug("  {}", returnedCuratedBadgeAwardEvents);

    assertEquals(this.badgeAwardGenericEvents.size(), returnedCuratedBadgeAwardEvents.size());
    validateResults(returnedCuratedBadgeAwardEvents);
  }

  @Test
  void testExpectedEventViaSpecificRequest() throws NostrException {
    List<EventIF> returnedCuratedBadgeAwardEvents = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(
                   Kind.CURATION_SETS_BADGE_AWARD_EVENT),
                new ReferencedPublicKeyFilter(
                   new PubKeyTag(
                      recipient.getPublicKey())))),
          awardEventRelayUrl,
          Duration.ofSeconds(10)));

    log.debug("returned events:");
    log.debug("  {}", returnedCuratedBadgeAwardEvents);

    assertEquals(this.badgeAwardGenericEvents.size(), returnedCuratedBadgeAwardEvents.size());
    validateResults(returnedCuratedBadgeAwardEvents);
  }

  private void validateResults(List<EventIF> returnedCuratedBadgeAwardEvents) {
    List<String> eventIds = returnedCuratedBadgeAwardEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();

    assertTrue(eventIds.stream().anyMatch(this.badgeAwardGenericEvents.stream().map(BadgeAwardGenericEvent::getId).toList()::contains));

    assertTrue(returnedCuratedBadgeAwardEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(PubKeyTag.class))
       .map(PubKeyTag::getPublicKey).allMatch(recipient.getPublicKey()::equals));

    assertTrue(
       returnedCuratedBadgeAwardEvents.stream()
          .map(EventIF::asGenericEventRecord)
          .map(event -> event.requireFirstTag(AddressTag.class).getIdentifierTag())
          .anyMatch(
             this.badgeDefinitionGenericEvents.stream()
                .map(badgeDefinitionUpvoteEvent ->
                   AbstractSetsEvent.hashedAddressTag(badgeDefinitionUpvoteEvent.asAddressableEventAddressTag()))
                .toList()::contains));
  }
}
