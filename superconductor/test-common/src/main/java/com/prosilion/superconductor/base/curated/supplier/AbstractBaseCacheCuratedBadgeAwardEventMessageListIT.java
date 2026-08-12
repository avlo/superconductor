package com.prosilion.superconductor.base.curated.supplier;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
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
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseCacheCuratedBadgeAwardEventMessageListIT extends BaseIntegrationTestFixtures {
  protected final String awardEventRelayUrl;
  protected final String definitionEventRelayUrl;

  protected final List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardGenericEventList;

  abstract protected List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> createBadgeAwardEventList();
  abstract protected void validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardUpvoteEvents);

  protected AbstractBaseCacheCuratedBadgeAwardEventMessageListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) throws NostrException {
    super(superconductorInstanceIdentity);
    this.definitionEventRelayUrl = definitionEventRelayUrl;
    this.awardEventRelayUrl = awardEventRelayUrl;

    this.badgeAwardGenericEventList = createBadgeAwardEventList();
    setupBadgeAwardEvents(badgeAwardGenericEventList);
  }

  private void setupBadgeAwardEvents(List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardUpvoteEvents) {
    badgeAwardUpvoteEvents.stream().map(BadgeAwardAbstractEvent::getBadgeDefinitionEvent)
       .forEach(badgeDefinitionGenericEvent ->
          assertTrue(
             new NostrEventPublisher(definitionEventRelayUrl)
                .send(
                   new EventMessage(badgeDefinitionGenericEvent), Duration.ofSeconds(5)).getFlag()));

    validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(badgeAwardUpvoteEvents);
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
          Duration.ofSeconds(5)));

    log.debug("returned events:");
    log.debug("  {}", returnedCuratedBadgeAwardEvents.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n")));

    validateResults(returnedCuratedBadgeAwardEvents);
  }

  private void validateResults(List<EventIF> returnedCuratedBadgeAwardEvents) {
    assertEquals(badgeAwardGenericEventList.size(), returnedCuratedBadgeAwardEvents.size());

    List<String> eventIds = returnedCuratedBadgeAwardEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();

    assertTrue(eventIds.stream().anyMatch(this.badgeAwardGenericEventList.stream().map(BadgeAwardGenericEvent::getId).toList()::contains));

    assertTrue(returnedCuratedBadgeAwardEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(PubKeyTag.class))
       .map(PubKeyTag::getPublicKey).allMatch(recipient.getPublicKey()::equals));

    assertTrue(
       returnedCuratedBadgeAwardEvents.stream()
          .map(EventIF::asGenericEventRecord)
          .map(event -> event.requireFirstTag(AddressTag.class).getIdentifierTag())
          .anyMatch(
             this.badgeAwardGenericEventList.stream().map(BadgeAwardAbstractEvent::getBadgeDefinitionEvent)
                .map(badgeDefinitionUpvoteEvent ->
                   AbstractSetsEvent.hashedAddressTag(badgeDefinitionUpvoteEvent.asAddressableEventAddressTag()))
                .toList()::contains));

    List<EventIF> returnedCuratedBadgeDefinitionEvents = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(
                   Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          awardEventRelayUrl,
          Duration.ofSeconds(5)));

    assertTrue(returnedCuratedBadgeDefinitionEvents.stream().map(EventIF::getId)
       .allMatch(returnedCuratedBadgeAwardEvents.stream()
          .map(event -> event.requireFirstTag(IdentifierTag.class).getUuid())
          .toList()::contains));
  }
}
