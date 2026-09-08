package com.prosilion.superconductor.base;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
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
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseBadgeAwardDownvoteEventSupplierLocalIT extends BaseIntegrationTestDirtiesContextFixtures {
  private final String eventId;
  private final String relayUrl;

  protected BaseBadgeAwardDownvoteEventSupplierLocalIT(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorInstanceIdentity);
    this.relayUrl = superconductorRelayUrl;

    Relay definitionEventRelay = new Relay(superconductorRelayUrl);
    Relay awardEventRelay = new Relay(superconductorRelayUrl);

    BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag,
       definitionEventRelay);

    NostrEventPublisher definitionEventNostrEventPublisher = new NostrEventPublisher(relayUrl);
    EventMessage eventMessageBadgeDefinitionDownvoteEvent = new EventMessage(badgeDefinitionDownvoteEvent);
    assertTrue(
       definitionEventNostrEventPublisher
          .send(
             eventMessageBadgeDefinitionDownvoteEvent)
          .getFlag());

    BadgeAwardCanonicalEvent badgeAwardDownvoteEvent = new BadgeAwardCanonicalEvent(
       submitter,
       recipient.getPublicKey(),
       badgeDefinitionDownvoteEvent,
       awardEventRelay);
    eventId = badgeAwardDownvoteEvent.getId();

    NostrEventPublisher awardEventNostrComprehensiveClient = new NostrEventPublisher(relayUrl);
    EventMessage eventMessageBadgeAwardDownvoteEvent = new EventMessage(badgeAwardDownvoteEvent);
    assertTrue(
       awardEventNostrComprehensiveClient
          .send(
             eventMessageBadgeAwardDownvoteEvent)
          .getFlag());
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequestGeneral() throws NostrException {
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
          relayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(submitter.getPublicKey())));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.requireFirstTag(PubKeyTag.class).getPublicKey().equals(recipient.getPublicKey())));

    AddressTag addressTag = returnedEventIFs.getFirst().asGenericEventRecord().getTypeSpecificTags(AddressTag.class).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(downvoteIdentifierTag, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequestSpecific() throws NostrException {
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
                      upvoteDefnCreator.getPublicKey(),
                      downvoteIdentifierTag)))),
          relayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(submitter.getPublicKey())));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.requireFirstTag(PubKeyTag.class).getPublicKey().equals(recipient.getPublicKey())));

    AddressTag addressTag = returnedEventIFs.getFirst().asGenericEventRecord().getTypeSpecificTags(AddressTag.class).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(downvoteIdentifierTag, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
  }
}
