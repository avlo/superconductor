package com.prosilion.superconductor.base;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericVoteEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.EventIF;
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
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import com.prosilion.superconductor.util.Utils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.util.TestKindType.UNIT_UPVOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseBadgeAwardUpvoteEventMessageIT {
  private final NostrRelayService nostrRelayService;

  private final Identity authorIdentity = Identity.generateRandomIdentity();
  private final PublicKey upvotedUserPubKey = Identity.generateRandomIdentity().getPublicKey();
  private final Identity superconductorInstanceIdentity;

  private final String eventId;

  protected BaseBadgeAwardUpvoteEventMessageIT(
      @NonNull String relayUrl,
      @NonNull BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull Identity superconductorInstanceIdentity) throws IOException, NostrException {
    this.nostrRelayService = new NostrRelayService(relayUrl);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;

    BadgeAwardGenericVoteEvent event = new BadgeAwardGenericVoteEvent(
        authorIdentity,
        upvotedUserPubKey,
        badgeDefinitionUpvoteEvent);
    eventId = event.getId();

    EventMessage eventMessage = new EventMessage(event);
    assertTrue(
        this.nostrRelayService
            .send(
                eventMessage)
            .getFlag());
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequest() throws IOException, NostrException {
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
                            new IdentifierTag(
                                UNIT_UPVOTE.getName())))))));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorIdentity.getPublicKey())));

    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, returnedEventIFs.getFirst()).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(UNIT_UPVOTE.getName(), Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow().getUuid());

    nostrRelayService.disconnect();
  }
}
