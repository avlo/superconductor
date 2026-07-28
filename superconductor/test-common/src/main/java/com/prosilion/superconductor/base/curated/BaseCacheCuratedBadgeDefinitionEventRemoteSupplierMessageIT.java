package com.prosilion.superconductor.base.curated;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseCacheCuratedBadgeDefinitionEventRemoteSupplierMessageIT extends BaseIntegrationTestFixtures {
  private final CacheServiceIF cacheServiceIF; // TODO: here/remove for/after convenience testing

  private final String definitionEventRelayUrl;

  private final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEventWithRelayTag;
  private final BadgeDefinitionGenericEvent badgeDefinitionDownvoteEventWithoutRelayTag;

  protected BaseCacheCuratedBadgeDefinitionEventRemoteSupplierMessageIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull String superconductorRelayUrl,
     @NonNull String definitionEventRelayUrl) throws NostrException {
    super(superconductorInstanceIdentity);

    this.cacheServiceIF = cacheServiceIF;
    this.definitionEventRelayUrl = definitionEventRelayUrl;

    this.badgeDefinitionUpvoteEventWithRelayTag = new BadgeDefinitionGenericEvent(
       superconductorInstanceIdentity,
       upvoteIdentifierTag,
       new Relay("ws://superconductor-app-two:5555"));

    this.badgeDefinitionDownvoteEventWithoutRelayTag = new BadgeDefinitionGenericEvent(
       superconductorInstanceIdentity,
       downvoteIdentifierTag);

    setupBadgeDefinitionEvent(badgeDefinitionUpvoteEventWithRelayTag);
    setupBadgeDefinitionEvent(badgeDefinitionDownvoteEventWithoutRelayTag);
  }

  @Test
  void testEventMessageBadgeDefinitionUpvoteEventWithRelayTag() throws NostrException {
    assertTrue(true);
  }

  @Test
  void testEventMessageBadgeDefinitionUpvoteEventWithoutRelayTag() throws NostrException {
  }

  private void setupBadgeDefinitionEvent(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    NostrEventPublisher definitionEventNostrEventPublisher = new NostrEventPublisher(definitionEventRelayUrl);
    EventMessage eventMessageBadgeDefinitionUpvoteEventWithRelayTag = new EventMessage(badgeDefinitionGenericEvent);
    assertTrue(
       definitionEventNostrEventPublisher
          .send(
             eventMessageBadgeDefinitionUpvoteEventWithRelayTag)
          .getFlag());

    final String subscriberId = Factory.generateRandomHex64String();

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             subscriberId,
             new Filters(
                new KindFilter(
                   Kind.BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().map(EventIF::getId).anyMatch(badgeDefinitionGenericEvent.getId()::equals));

//    assertTrue(returnedEventIFs.stream().map(event -> event.getTypeSpecificTags(AddressTag.class)).flatMap(Collection::stream)
//       .anyMatch(badgeDefinitionGenericEvent.asAddressableEventAddressTag()::equals));
//
//    assertTrue(returnedEventIFs.stream().map(event -> event.getTypeSpecificTags(RelayTag.class)).flatMap(Collection::stream)
//       .map(RelayTag::getRelay).map(Relay::getUrl)
//       .anyMatch(definitionEventRelayUrl::equals));
  }
}
