package com.prosilion.superconductor.supplier;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestDirtiesContextFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static com.prosilion.nostr.util.Util.generateRandomHex64String;
import static com.prosilion.superconductor.BaseCacheFollowSetsEventServiceIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseCacheBadgeAwardCanonicalEventMessageListIT extends BaseIntegrationTestDirtiesContextFixtures {
  protected final String awardEventRelayUrl;
  protected final String definitionEventRelayUrl;

  protected final List<BadgeAwardCanonicalEvent> badgeAwardGenericEventList;

  abstract protected List<BadgeAwardCanonicalEvent> createBadgeAwardEventList();
  abstract protected void validatePersistedCurationSetsBadgeDefinitionEvents(List<BadgeAwardCanonicalEvent> badgeAwardUpvoteEvents);

  CacheServiceIF cacheServiceIF;

  protected AbstractBaseCacheBadgeAwardCanonicalEventMessageListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl,
     CacheServiceIF cacheServiceIF) throws NostrException {
    super(superconductorInstanceIdentity);
    this.definitionEventRelayUrl = definitionEventRelayUrl;
    this.awardEventRelayUrl = awardEventRelayUrl;
    this.cacheServiceIF = cacheServiceIF;

    this.badgeAwardGenericEventList = createBadgeAwardEventList();
    setupBadgeAwardEventBadgeDefinitionEvents(badgeAwardGenericEventList);
  }

  private void setupBadgeAwardEventBadgeDefinitionEvents(List<BadgeAwardCanonicalEvent> badgeAwardUpvoteEvents) {
    badgeAwardUpvoteEvents.stream().map(BadgeAwardAbstractEvent::getBadgeDefinitionEvent)
       .forEach(badgeDefinitionGenericEvent ->
          assertTrue(
             new NostrEventPublisher(definitionEventRelayUrl)
                .send(
                   new EventMessage(badgeDefinitionGenericEvent), Duration.ofSeconds(10)).getFlag()));

    validatePersistedCurationSetsBadgeDefinitionEvents(badgeAwardUpvoteEvents);
  }

  @Test
  void testExpectedEventViaSpecificRequest() throws NostrException {
    List<EventIF> returnedCuratedBadgeAwardEvents = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(
                   Kind.BADGE_AWARD_EVENT),
                new ReferencedPublicKeyFilter(
                   new PubKeyTag(
                      recipient.getPublicKey())))),
          awardEventRelayUrl,
          Duration.ofSeconds(10)));

    log.debug("returned events:");
    log.debug("  {}", returnedCuratedBadgeAwardEvents.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n")));

    validateResults(returnedCuratedBadgeAwardEvents);
  }

  private void validateResults(List<EventIF> returnedBadgeAwardCanonicalEvents) {
    assertEquals(badgeAwardGenericEventList.size(), returnedBadgeAwardCanonicalEvents.size());

    List<String> eventIds = returnedBadgeAwardCanonicalEvents.stream()
//       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventIF::getId).toList();

    assertTrue(eventIds.stream().anyMatch(this.badgeAwardGenericEventList.stream().map(BadgeAwardCanonicalEvent::getId).toList()::contains));

    assertTrue(returnedBadgeAwardCanonicalEvents.stream()
       .map(event -> event.requireFirstTag(PubKeyTag.class))
       .map(PubKeyTag::getPublicKey).allMatch(recipient.getPublicKey()::equals));

    assertTrue(
       returnedBadgeAwardCanonicalEvents.stream()
          .map(event -> event.requireFirstTag(AddressTag.class).getIdentifierTag())
          .anyMatch(
             this.badgeAwardGenericEventList.stream().map(BadgeAwardAbstractEvent::getBadgeDefinitionEvent)
                .map(BadgeDefinitionGenericEvent::getIdentifierTag)
                .toList()::contains));

    List<EventIF> returnedBadgeDefinitionEvents = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(
                   Kind.BADGE_DEFINITION_EVENT))),
          awardEventRelayUrl,
          Duration.ofSeconds(10)));

    assertTrue(
       returnedBadgeDefinitionEvents.stream()
          .map(eventIF ->
             eventIF.requireFirstTag(IdentifierTag.class))
          .allMatch(returnedBadgeAwardCanonicalEvents.stream()
             .map(event -> event.requireFirstTag(AddressTag.class).getIdentifierTag())
             .toList()::contains));
  }
}
