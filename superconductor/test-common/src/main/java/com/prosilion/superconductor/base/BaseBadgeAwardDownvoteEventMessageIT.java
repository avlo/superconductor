package com.prosilion.superconductor.base;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.Utils;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseBadgeAwardDownvoteEventMessageIT {
  public static final String IDENTIFIER_TAG_UUID = Factory.generateRandomHex64String();
  public static final IdentifierTag IDENTIFIER_TAG = new IdentifierTag(IDENTIFIER_TAG_UUID);

  private final Identity authorIdentity = Identity.generateRandomIdentity();
  private final PublicKey downvotedUserPubKey = Identity.generateRandomIdentity().getPublicKey();
  private final Identity superconductorInstanceIdentity;

  private final String eventId;
  private final String relayUrl;

  protected BaseBadgeAwardDownvoteEventMessageIT(
      @NonNull String relayUrl,
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull Identity superconductorInstanceIdentity,
      Duration requestTimeoutDuration) throws IOException, NostrException {
    this.relayUrl = relayUrl;
    NostrEventPublisher nostrEventPublisher = new NostrEventPublisher(relayUrl);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    Relay relay = new Relay(relayUrl);

    BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent = new BadgeDefinitionGenericEvent(
        superconductorInstanceIdentity,
        IDENTIFIER_TAG, relay);

    cacheServiceIF.save(badgeDefinitionDownvoteEvent);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardDownvoteEvent = new BadgeAwardGenericEvent<>(
        authorIdentity,
        downvotedUserPubKey,
        relay,
        badgeDefinitionDownvoteEvent);
    eventId = badgeAwardDownvoteEvent.getId();

    EventMessage eventMessageBadgeAwardDownvoteEvent = new EventMessage(badgeAwardDownvoteEvent);
    assertTrue(
        nostrEventPublisher
            .send(
                eventMessageBadgeAwardDownvoteEvent)
            .getFlag());
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequestGenral() throws IOException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = new ReqMessage(
        subscriberId,
        new Filters(
            new KindFilter(
                Kind.BADGE_AWARD_EVENT),
            new ReferencedPublicKeyFilter(
                new PubKeyTag(
                    downvotedUserPubKey))));
    List<EventIF> returnedEventIFs = Utils.getEventIFs(
        new NostrSingleRequestService().send(reqMessage, relayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorIdentity.getPublicKey())));

    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, returnedEventIFs.getFirst()).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(IDENTIFIER_TAG, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
    assertEquals(IDENTIFIER_TAG_UUID, Optional.of(addressTag.getIdentifierTag()).orElseThrow().getUuid());
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequestSpecific() throws IOException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    List<EventIF> returnedEventIFs = Utils.getEventIFs(
        new NostrSingleRequestService().send(
            new ReqMessage(
                subscriberId,
                new Filters(
                    new KindFilter(
                        Kind.BADGE_AWARD_EVENT),
                    new ReferencedPublicKeyFilter(
                        new PubKeyTag(
                            downvotedUserPubKey)),
                    new AddressTagFilter(
                        new AddressTag(
                            Kind.BADGE_DEFINITION_EVENT,
                            superconductorInstanceIdentity.getPublicKey(),
                            IDENTIFIER_TAG)))),
            relayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorIdentity.getPublicKey())));

    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, returnedEventIFs.getFirst()).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(IDENTIFIER_TAG, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
    assertEquals(IDENTIFIER_TAG_UUID, Optional.of(addressTag.getIdentifierTag()).orElseThrow().getUuid());
  }
}
