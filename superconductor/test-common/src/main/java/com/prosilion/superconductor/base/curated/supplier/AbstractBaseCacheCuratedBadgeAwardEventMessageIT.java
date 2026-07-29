package com.prosilion.superconductor.base.curated.supplier;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseCacheCuratedBadgeAwardEventMessageIT extends BaseIntegrationTestFixtures {
  protected final Identity superconductorInstanceIdentity;

  protected final String awardEventRelayUrl;
  protected final String definitionEventRelayUrl;

  protected final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent;
  protected final BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent;
  BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEventWithRelayTag;
  BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEventWithoutRelayTag;

  abstract protected BadgeDefinitionGenericEvent createBadgeDefinitionUpvoteEvent();
  abstract protected BadgeDefinitionGenericEvent createBadgeDefinitionDownvoteEvent();
  abstract protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventContainingRelayTag();
  abstract protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTag();

  protected AbstractBaseCacheCuratedBadgeAwardEventMessageIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) throws NostrException {
    super(superconductorInstanceIdentity);

    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.definitionEventRelayUrl = definitionEventRelayUrl;
    this.awardEventRelayUrl = awardEventRelayUrl;

    this.badgeDefinitionUpvoteEvent = createBadgeDefinitionUpvoteEvent();
    setupBadgeDefinitionGenericEvent(badgeDefinitionUpvoteEvent, definitionEventRelayUrl);

    this.badgeDefinitionDownvoteEvent = createBadgeDefinitionDownvoteEvent();
    setupBadgeDefinitionGenericEvent(badgeDefinitionDownvoteEvent, definitionEventRelayUrl);

    this.badgeAwardEventWithRelayTag = createAwardEventContainingRelayTag();
    setupBadgeAwardEvent(badgeAwardEventWithRelayTag);

    this.badgeAwardEventWithoutRelayTag = createAwardEventWithoutRelayTag();
    setupBadgeAwardEvent(badgeAwardEventWithoutRelayTag);
  }

  private void setupBadgeDefinitionGenericEvent(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent, String definitionEventRelayUrl) {
    NostrEventPublisher definitionEventNostrEventPublisher = new NostrEventPublisher(definitionEventRelayUrl);
    EventMessage eventMessageBadgeDefinitionUpvoteEvent = new EventMessage(badgeDefinitionGenericEvent);
    assertTrue(
       definitionEventNostrEventPublisher
          .send(
             eventMessageBadgeDefinitionUpvoteEvent)
          .getFlag());

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

    assertTrue(eventIds.contains(badgeDefinitionGenericEvent.getId()));
  }

  private void setupBadgeAwardEvent(BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent) {
    NostrEventPublisher awardEventNostrComprehensiveClient = new NostrEventPublisher(awardEventRelayUrl);
    EventMessage eventMessageBadgeAwardUpvoteEvent = new EventMessage(badgeAwardUpvoteEvent);
    assertTrue(
       awardEventNostrComprehensiveClient
          .send(
             eventMessageBadgeAwardUpvoteEvent)
          .getFlag());
  }

  @Test
  void testExpectedEventViaGeneralRequest() throws NostrException {
    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_AWARD_EVENT))),
          awardEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    List<String> eventIds = returnedEventIFs.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();

    assertTrue(eventIds.contains(badgeAwardEventWithRelayTag.getId()));
    assertTrue(eventIds.contains(badgeAwardEventWithoutRelayTag.getId()));
//    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(badgeAwardEventWithoutRelayTag.getId())));
//    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(submitter.getPublicKey())));
//
//    AddressTag addressTag = returnedEventIFs.getFirst().asGenericEventRecord().getTypeSpecificTags(AddressTag.class).getFirst();
//
//    assertEquals(Kind.BADGE_AWARD_EVENT, addressTag.getKind());
//    assertEquals(upvoteIdentifierTag, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
  }

  @Test
  void testExpectedEventViaSpecificRequest() throws NostrException {
    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(
                   Kind.CURATION_SETS_BADGE_AWARD_EVENT),
                new ReferencedPublicKeyFilter(
                   new PubKeyTag(
                      recipient.getPublicKey())))),
          awardEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    List<String> eventIds = returnedEventIFs.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();

    assertTrue(eventIds.contains(badgeAwardEventWithRelayTag.getId()));
    assertTrue(eventIds.contains(badgeAwardEventWithoutRelayTag.getId()));

//    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(submitter.getPublicKey())));
//
//    AddressTag addressTag = returnedEventIFs.getFirst().asGenericEventRecord().getTypeSpecificTags(AddressTag.class).getFirst();
//
//    assertEquals(Kind.BADGE_AWARD_EVENT, addressTag.getKind());
//    assertEquals(upvoteIdentifierTag, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
  }
}
