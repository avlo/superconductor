package com.prosilion.superconductor.base.curated.supplier;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
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
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseCacheCuratedBadgeAwardEventMessageIT extends BaseIntegrationTestFixtures {
  protected final String awardEventRelayUrl;
  protected final String definitionEventRelayUrl;

  protected final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent;
  protected final BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent;
  protected final BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEventWithRelayTag;
  protected final BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEventWithoutRelayTag;

  protected AbstractBaseCacheCuratedBadgeAwardEventMessageIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) throws NostrException {
    super(superconductorInstanceIdentity);

    this.definitionEventRelayUrl = definitionEventRelayUrl;
    this.awardEventRelayUrl = awardEventRelayUrl;

    this.badgeDefinitionUpvoteEvent = createDefinitionEventContainingRelayTag();
    setupBadgeDefinitionEvent(badgeDefinitionUpvoteEvent, definitionEventRelayUrl);

    this.badgeDefinitionDownvoteEvent = createDefinitionEventWithoutRelayTag();
    setupBadgeDefinitionEvent(badgeDefinitionDownvoteEvent, definitionEventRelayUrl);

    this.badgeAwardEventWithRelayTag = createAwardEventContainingRelayTag();
    setupBadgeAwardEvent(badgeAwardEventWithRelayTag, awardEventRelayUrl);

    this.badgeAwardEventWithoutRelayTag = createAwardEventWithoutRelayTag();
    setupBadgeAwardEvent(badgeAwardEventWithoutRelayTag, awardEventRelayUrl);
  }

  abstract protected BadgeDefinitionGenericEvent createDefinitionEventContainingRelayTag();
  abstract protected BadgeDefinitionGenericEvent createDefinitionEventWithoutRelayTag();
  abstract protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventContainingRelayTag();
  abstract protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTag();

  private void setupBadgeDefinitionEvent(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent, String definitionEventRelayUrl) {
    assertTrue(
       new NostrEventPublisher(definitionEventRelayUrl)
          .send(
             new EventMessage(badgeDefinitionGenericEvent), Duration.ofSeconds(10))
          .getFlag());

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl,
          Duration.ofSeconds(10)));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(
       returnedEventIFs.stream()
          .map(event -> event.requireFirstTag(EventTag.class))
          .map(EventTag::getEventId)
          .anyMatch(badgeDefinitionGenericEvent.getId()::equals));
  }

  private void setupBadgeAwardEvent(BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent, String awardEventRelayUrl) {
    assertTrue(
       new NostrEventPublisher(awardEventRelayUrl)
          .send(
             new EventMessage(badgeAwardUpvoteEvent), Duration.ofSeconds(10))
          .getFlag());
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

    validateResults(returnedCuratedBadgeAwardEvents);
  }

  private void validateResults(List<EventIF> returnedCuratedBadgeAwardEvents) {
    List<String> eventIds = returnedCuratedBadgeAwardEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();

    assertTrue(eventIds.contains(badgeAwardEventWithRelayTag.getId()));
    assertTrue(eventIds.contains(badgeAwardEventWithoutRelayTag.getId()));

    assertTrue(returnedCuratedBadgeAwardEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(PubKeyTag.class))
       .map(PubKeyTag::getPublicKey).allMatch(recipient.getPublicKey()::equals));

    assertTrue(returnedCuratedBadgeAwardEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(AddressTag.class).getIdentifierTag())
       .anyMatch(
          AbstractSetsEvent.hashedAddressTag(badgeDefinitionUpvoteEvent.asAddressableEventAddressTag())::equals));

    assertTrue(returnedCuratedBadgeAwardEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(AddressTag.class).getIdentifierTag())
       .anyMatch(
          AbstractSetsEvent.hashedAddressTag(badgeDefinitionDownvoteEvent.asAddressableEventAddressTag())::equals));
  }
}
