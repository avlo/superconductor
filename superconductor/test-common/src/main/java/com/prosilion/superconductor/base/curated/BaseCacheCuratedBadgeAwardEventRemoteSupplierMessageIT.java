package com.prosilion.superconductor.base;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseCacheCuratedBadgeAwardEventRemoteSupplierMessageIT extends BaseIntegrationTestFixtures {
  private final Identity superconductorInstanceIdentity;
  private final CacheServiceIF cacheServiceIF;

  private final String eventId;
  private final String definitionEventRelayUrl;
  private final String awardEventRelayUrl;

  protected BaseCacheCuratedBadgeAwardEventRemoteSupplierMessageIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull String superconductorRelayUrl,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) throws IOException, NostrException {
    super(superconductorInstanceIdentity);
    
    this.cacheServiceIF = cacheServiceIF;
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.definitionEventRelayUrl = definitionEventRelayUrl;
    this.awardEventRelayUrl = awardEventRelayUrl;

    Relay definitionEventRelay = new Relay("ws://superconductor-app-two:5555");
    Relay awardEventRelay = new Relay("ws://superconductor-app-three:5555");

    BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent = new BadgeDefinitionGenericEvent(
       superconductorInstanceIdentity,
       upvoteIdentifierTag,
       definitionEventRelay);

    NostrEventPublisher definitionEventNostrEventPublisher = new NostrEventPublisher(definitionEventRelayUrl);
    EventMessage eventMessageBadgeDefinitionUpvoteEvent = new EventMessage(badgeDefinitionUpvoteEvent);
    assertTrue(
       definitionEventNostrEventPublisher
          .send(
             eventMessageBadgeDefinitionUpvoteEvent)
          .getFlag());

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       badgeDefinitionUpvoteEvent,
       awardEventRelay);
    eventId = badgeAwardUpvoteEvent.getId();

    NostrEventPublisher awardEventNostrComprehensiveClient = new NostrEventPublisher(awardEventRelayUrl);
    EventMessage eventMessageBadgeAwardUpvoteEvent = new EventMessage(badgeAwardUpvoteEvent);
    assertTrue(
       awardEventNostrComprehensiveClient
          .send(
             eventMessageBadgeAwardUpvoteEvent)
          .getFlag());
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequestGeneral() throws IOException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             subscriberId,
             new Filters(
                new KindFilter(
                   Kind.BADGE_AWARD_EVENT),
                new ReferencedPublicKeyFilter(
                   new PubKeyTag(
                      recipient.getPublicKey())))),
          awardEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(submitter.getPublicKey())));

    AddressTag addressTag = returnedEventIFs.getFirst().asGenericEventRecord().getTypeSpecificTags(AddressTag.class).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(upvoteIdentifierTag, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequestSpecific() throws IOException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             subscriberId,
             new Filters(
                new KindFilter(
                   Kind.BADGE_AWARD_EVENT),
                new ReferencedPublicKeyFilter(
                   new PubKeyTag(
                      recipient.getPublicKey())),
                new AddressTagFilter(
                   new AddressTag(
                      Kind.BADGE_DEFINITION_EVENT,
                      superconductorInstanceIdentity.getPublicKey(),
                      upvoteIdentifierTag)))),
          awardEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(submitter.getPublicKey())));

    AddressTag addressTag = returnedEventIFs.getFirst().asGenericEventRecord().getTypeSpecificTags(AddressTag.class).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(upvoteIdentifierTag, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
  }

  private BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAndSaveBackingBadgeAward() {
    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent =
       new BadgeDefinitionGenericEvent(
          Identity.generateRandomIdentity(),
          upvoteIdentifierTag,
          relay);
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       new BadgeAwardGenericEvent<>(
          Identity.generateRandomIdentity(),
          recipient.getPublicKey(),
          badgeDefinitionGenericEvent,
          relay);
    cacheServiceIF.save(badgeDefinitionGenericEvent);
    cacheServiceIF.save(badgeAwardGenericEvent);
    return badgeAwardGenericEvent;
  }
}
