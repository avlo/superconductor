package com.prosilion.superconductor.base.curated;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseCacheCuratedBadgeDefinitionEventRemoteSupplierIsSameRelayMessageIT extends BaseIntegrationTestFixtures {
  private final CacheServiceIF cacheServiceIF; // TODO: here/remove for/after convenience testing

  private final String definitionEventRelayUrl;

  private final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEventWithRelayTag;
  private final BadgeDefinitionGenericEvent badgeDefinitionDownvoteEventWithoutRelayTag;

  protected BaseCacheCuratedBadgeDefinitionEventRemoteSupplierIsSameRelayMessageIT(
     @NonNull String superconductorRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorInstanceIdentity);
    this.cacheServiceIF = cacheServiceIF;
    this.definitionEventRelayUrl = superconductorRelayUrl;

    Relay definitionEventRelay = new Relay(definitionEventRelayUrl);

    this.badgeDefinitionUpvoteEventWithRelayTag = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       definitionEventRelay);

    this.badgeDefinitionDownvoteEventWithoutRelayTag = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag);

    setupBadgeDefinitionEvent(badgeDefinitionUpvoteEventWithRelayTag);
    setupBadgeDefinitionEvent(badgeDefinitionDownvoteEventWithoutRelayTag);
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequestGeneral() throws NostrException {
    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    List<String> eventIds = returnedEventIFs.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();
    
    assertTrue(eventIds.contains(badgeDefinitionUpvoteEventWithRelayTag.getId()));
    assertTrue(eventIds.contains(badgeDefinitionDownvoteEventWithoutRelayTag.getId()));
  }

  private void setupBadgeDefinitionEvent(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    NostrEventPublisher definitionEventNostrEventPublisher = new NostrEventPublisher(definitionEventRelayUrl);
    EventMessage eventMessageBadgeDefinitionUpvoteEventWithRelayTag = new EventMessage(badgeDefinitionGenericEvent);
    assertTrue(
       definitionEventNostrEventPublisher
          .send(
             eventMessageBadgeDefinitionUpvoteEventWithRelayTag, Duration.ofMinutes(30))
          .getFlag());

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(
                   Kind.BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().map(EventIF::getId).anyMatch(badgeDefinitionGenericEvent.getId()::equals));
  }
}
