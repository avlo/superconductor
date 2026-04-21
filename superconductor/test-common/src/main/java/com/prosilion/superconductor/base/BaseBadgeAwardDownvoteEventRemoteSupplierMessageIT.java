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
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseBadgeAwardDownvoteEventRemoteSupplierMessageIT {
  public static final String IDENTIFIER_TAG_UUID = Factory.generateRandomHex64String();
  public static final IdentifierTag IDENTIFIER_TAG = new IdentifierTag(IDENTIFIER_TAG_UUID);

  private final Identity authorIdentity = Identity.generateRandomIdentity();
  private final PublicKey downvotedUserPubKey = Identity.generateRandomIdentity().getPublicKey();
  private final Identity superconductorInstanceIdentity;

  private final String eventId;
  private final String definitionEventRelayUrl;
  private final String awardEventRelayUrl;

  protected BaseBadgeAwardDownvoteEventRemoteSupplierMessageIT(
      @NonNull String superconductorRelayUrl,
      @NonNull String definitionEventRelayUrl,
      @NonNull String awardEventRelayUrl,
      @NonNull Identity superconductorInstanceIdentity) throws IOException, NostrException {
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.definitionEventRelayUrl = definitionEventRelayUrl;
    this.awardEventRelayUrl = awardEventRelayUrl;

    Relay definitionEventRelay = new Relay("ws://superconductor-app-two:5555");
    Relay awardEventRelay = new Relay("ws://superconductor-app-three:5555");

    BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent = new BadgeDefinitionGenericEvent(
        superconductorInstanceIdentity,
        IDENTIFIER_TAG,
        definitionEventRelay);

    NostrEventPublisher definitionEventNostrEventPublisher = new NostrEventPublisher(definitionEventRelayUrl);
    EventMessage eventMessageBadgeDefinitionDownvoteEvent = new EventMessage(badgeDefinitionDownvoteEvent);
    assertTrue(
        definitionEventNostrEventPublisher
            .send(
                eventMessageBadgeDefinitionDownvoteEvent)
            .getFlag());

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardDownvoteEvent = new BadgeAwardGenericEvent<>(
        authorIdentity,
        downvotedUserPubKey,
        awardEventRelay,
        badgeDefinitionDownvoteEvent);
    eventId = badgeAwardDownvoteEvent.getId();

    NostrEventPublisher awardEventNostrComprehensiveClient = new NostrEventPublisher(awardEventRelayUrl);
    EventMessage eventMessageBadgeAwardDownvoteEvent = new EventMessage(badgeAwardDownvoteEvent);
    assertTrue(
        awardEventNostrComprehensiveClient
            .send(
                eventMessageBadgeAwardDownvoteEvent)
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
                            downvotedUserPubKey)))),
            awardEventRelayUrl));

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

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
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
            awardEventRelayUrl));

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
