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
import com.prosilion.superconductor.base.util.NostrRelayService;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.Utils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseBadgeAwardUpvoteEventMessageIT {
  public static final String IDENTIFIER_TAG_UUID = Factory.generateRandomHex64String();
  public static final IdentifierTag IDENTIFIER_TAG = new IdentifierTag(IDENTIFIER_TAG_UUID);
  private final NostrRelayService nostrRelayService;

  private final Identity authorIdentity = Identity.generateRandomIdentity();
  private final PublicKey upvotedUserPubKey = Identity.generateRandomIdentity().getPublicKey();
  private final Identity superconductorInstanceIdentity;

  private final String eventId;

  protected BaseBadgeAwardUpvoteEventMessageIT(
      @NonNull String relayUrl,
      @NonNull Identity superconductorInstanceIdentity) throws IOException, NostrException {
    this.nostrRelayService = new NostrRelayService(relayUrl);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    Relay relay = new Relay(relayUrl);

    BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent = new BadgeDefinitionGenericEvent(
        superconductorInstanceIdentity,
        IDENTIFIER_TAG, relay);

    EventMessage eventMessageBadgeDefinitionUpvoteEvent = new EventMessage(badgeDefinitionUpvoteEvent);
    assertTrue(
        this.nostrRelayService
            .send(
                eventMessageBadgeDefinitionUpvoteEvent)
            .getFlag());

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
        authorIdentity,
        upvotedUserPubKey,
        relay,
        badgeDefinitionUpvoteEvent);
    eventId = badgeAwardUpvoteEvent.getId();

    EventMessage eventMessageBadgeAwardUpvoteEvent = new EventMessage(badgeAwardUpvoteEvent);
    assertTrue(
        this.nostrRelayService
            .send(
                eventMessageBadgeAwardUpvoteEvent)
            .getFlag());
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequestGeneral() throws IOException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    List<EventIF> returnedEventIFs = Utils.getEventIFs(
        nostrRelayService.send(
            new ReqMessage(
                subscriberId,
                new Filters(
                    new KindFilter(
                        Kind.BADGE_AWARD_EVENT),
                    new ReferencedPublicKeyFilter(
                        new PubKeyTag(
                            upvotedUserPubKey))))));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorIdentity.getPublicKey())));

    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, returnedEventIFs.getFirst()).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(IDENTIFIER_TAG, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
    assertEquals(IDENTIFIER_TAG_UUID, Optional.of(addressTag.getIdentifierTag()).orElseThrow().getUuid());

    nostrRelayService.disconnect();
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequestSpecific() throws IOException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    List<EventIF> returnedEventIFs = Utils.getEventIFs(
        nostrRelayService.send(
            new ReqMessage(
                subscriberId,
                new Filters(
                    new KindFilter(
                        Kind.BADGE_AWARD_EVENT),
                    new ReferencedPublicKeyFilter(
                        new PubKeyTag(
                            upvotedUserPubKey)),
                    new AddressTagFilter(
                        new AddressTag(
                            Kind.BADGE_DEFINITION_EVENT,
                            superconductorInstanceIdentity.getPublicKey(),
                            IDENTIFIER_TAG))))));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorIdentity.getPublicKey())));

    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, returnedEventIFs.getFirst()).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(IDENTIFIER_TAG, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
    assertEquals(IDENTIFIER_TAG_UUID, Optional.of(addressTag.getIdentifierTag()).orElseThrow().getUuid());

    nostrRelayService.disconnect();
  }
}
