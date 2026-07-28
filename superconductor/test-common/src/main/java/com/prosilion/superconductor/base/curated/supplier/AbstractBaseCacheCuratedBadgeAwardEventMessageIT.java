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
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseCacheCuratedBadgeAwardEventMessageIT extends BaseIntegrationTestFixtures {
  protected final Identity superconductorInstanceIdentity;

  protected final String awardEventRelayUrl;
  protected final String definitionEventRelayUrl;

  protected final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent;
  BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEventWithRelayTag;
  BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEventWithoutRelayTag;

  abstract protected BadgeDefinitionGenericEvent createBadgeDefinitionGenericEvent();
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

    this.badgeDefinitionUpvoteEvent = createBadgeDefinitionGenericEvent();
    setupBadgeDefinitionGenericEvent(badgeDefinitionUpvoteEvent, definitionEventRelayUrl);

    this.badgeAwardEventWithRelayTag = createAwardEventContainingRelayTag();
    setupBadgeAwardEvent(badgeAwardEventWithRelayTag);

    this.badgeAwardEventWithoutRelayTag = createAwardEventWithoutRelayTag();
    setupBadgeAwardEvent(badgeAwardEventWithoutRelayTag);
  }

  private void setupBadgeDefinitionGenericEvent(
     BadgeDefinitionGenericEvent badgeDefinitionGenericEvent,
     String definitionEventRelayUrl) {
    NostrEventPublisher definitionEventNostrEventPublisher = new NostrEventPublisher(definitionEventRelayUrl);
    EventMessage eventMessageBadgeDefinitionUpvoteEvent = new EventMessage(badgeDefinitionGenericEvent);
    assertTrue(
       definitionEventNostrEventPublisher
          .send(
             eventMessageBadgeDefinitionUpvoteEvent)
          .getFlag());
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
          awardEventRelayUrl,
          Duration.ofMinutes(30)));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(badgeAwardEventWithRelayTag.getId())));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(badgeAwardEventWithoutRelayTag.getId())));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(submitter.getPublicKey())));

    AddressTag addressTag = returnedEventIFs.getFirst().asGenericEventRecord().getTypeSpecificTags(AddressTag.class).getFirst();

    assertEquals(Kind.BADGE_AWARD_EVENT, addressTag.getKind());
    assertEquals(upvoteIdentifierTag, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
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
          awardEventRelayUrl,
          Duration.ofMinutes(30)));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(badgeAwardEventWithRelayTag.getId())));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(badgeAwardEventWithoutRelayTag.getId())));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(submitter.getPublicKey())));

    AddressTag addressTag = returnedEventIFs.getFirst().asGenericEventRecord().getTypeSpecificTags(AddressTag.class).getFirst();

    assertEquals(Kind.BADGE_AWARD_EVENT, addressTag.getKind());
    assertEquals(upvoteIdentifierTag, Optional.ofNullable(addressTag.getIdentifierTag()).orElseThrow());
  }

//  private BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAndSaveBackingBadgeAward() {
//    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent =
//       new BadgeDefinitionGenericEvent(
//          Identity.generateRandomIdentity(),
//          upvoteIdentifierTag,
//          relay);
//    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
//       new BadgeAwardGenericEvent<>(
//          Identity.generateRandomIdentity(),
//          recipient.getPublicKey(),
//          badgeDefinitionGenericEvent,
//          relay);
//    cacheServiceIF.save(badgeDefinitionGenericEvent);
//    cacheServiceIF.save(badgeAwardGenericEvent);
//    return badgeAwardGenericEvent;
//  }
}
