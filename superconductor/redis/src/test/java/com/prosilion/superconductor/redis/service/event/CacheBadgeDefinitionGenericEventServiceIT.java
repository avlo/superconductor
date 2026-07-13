package com.prosilion.superconductor.redis.service.event;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheBadgeDefinitionGenericEventServiceIT {
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);

  public final Identity authorIdentity = Identity.generateRandomIdentity();
  private final PublicKey reputationRecipientPublicKey = Identity.generateRandomIdentity().getPublicKey();

  private final CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  @Autowired
  public CacheBadgeDefinitionGenericEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUri,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheBadgeDefinitionGenericEventService") CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) {
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeDefinitionGenericEventService = cacheBadgeDefinitionGenericEventService;
    this.relay = new Relay(relayUri);
  }

  @Test
  public void testSaveBadgeDefinitionGenericEventContainingEventRelay() {
    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(authorIdentity, upvoteIdentifierTag, relay);
    eventServiceIF.processIncomingEvent(new EventMessage(awardUpvoteDefinitionEvent), relay);
    BadgeDefinitionGenericEvent dbDefinitionGenericEvent = cacheBadgeDefinitionGenericEventService.getEvent(awardUpvoteDefinitionEvent.getId(), relay).orElseThrow();

    assertEquals(dbDefinitionGenericEvent.getId(), awardUpvoteDefinitionEvent.getId());
    assertEquals(relay, awardUpvoteDefinitionEvent.getRelay().orElseThrow());
    assertEquals(relay, awardUpvoteDefinitionEvent.requireFirstTag(RelayTag.class).getRelay());
    assertEquals(relay, awardUpvoteDefinitionEvent.getRelayTag().map(RelayTag::getRelay).orElseThrow());
    assertEquals(relay, awardUpvoteDefinitionEvent.findFirstTag(RelayTag.class).orElseThrow().getRelay());
    assertEquals(relay, awardUpvoteDefinitionEvent.getTypeSpecificTags(RelayTag.class).getFirst().getRelay());
  }

  @Test
  public void testSaveBadgeDefinitionGenericEventMissingEventRelay() {
    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(authorIdentity, upvoteIdentifierTag);
    eventServiceIF.processIncomingEvent(new EventMessage(awardUpvoteDefinitionEvent), relay);
    Optional<BadgeDefinitionGenericEvent> dbDefinitionGenericEvent =
       cacheBadgeDefinitionGenericEventService.getEvent(awardUpvoteDefinitionEvent.getId(), relay);

    assertTrue(dbDefinitionGenericEvent.isPresent());
    assertNotEquals(dbDefinitionGenericEvent.map(BadgeDefinitionGenericEvent::getId).orElseThrow(), awardUpvoteDefinitionEvent.getId());
    assertTrue(dbDefinitionGenericEvent.map(BadgeDefinitionGenericEvent::getEventTags).stream().flatMap(Collection::stream).map(EventTag::eventId).anyMatch(awardUpvoteDefinitionEvent.getId()::equals));
  }

//  @Test
//  public void testSaveBadgeDefinitionGenericEventNullRelay() {
//    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
//       authorIdentity,
//       reputationRecipientPublicKey,
//       awardUpvoteDefinitionEvent,
//       relay);
//
//    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardUpvoteEvent), relay);
//    BadgeDefinitionGenericEvent dbDefinitionGenericEvent = cacheBadgeDefinitionGenericEventService.getBy(badgeAwardUpvoteEvent.getAddressTag()).orElseThrow();
//
//    assertEquals(badgeAwardUpvoteEvent.getBadgeDefinitionEvent(), dbDefinitionGenericEvent);
//    assertEquals(upvoteIdentifierTag, dbDefinitionGenericEvent.getIdentifierTag());
//
//    String BADGE_DEFINITION_VOTE = "BADGE_DEFINITION_DOWNVOTE";
//    IdentifierTag downvoteIdentifierTag = new IdentifierTag(BADGE_DEFINITION_VOTE);
//
//    BadgeDefinitionGenericEvent awardDownvoteDefinitionEvent = new BadgeDefinitionGenericEvent(authorIdentity, downvoteIdentifierTag, relay);
//
//    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEventMinusOne = new BadgeAwardGenericEvent<>(
//       authorIdentity,
//       reputationRecipientPublicKey,
//       awardDownvoteDefinitionEvent,
//       relay);
//  }
}
