package com.prosilion.superconductor.redis.service.event;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheBadgeAwardGenericEventServiceIT {
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);
  public final Identity identity = Identity.generateRandomIdentity();

  private final BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;
  private final CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventServiceIF;
  private final EventServiceIF eventServiceIF;
  private final Relay relay;

  public CacheBadgeAwardGenericEventServiceIT(
      @Value("${superconductor.relay.url}") String relayUri,
      @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheTagMappedEventServiceIF<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> cacheBadgeAwardGenericEventServiceIF) {
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeAwardGenericEventServiceIF = (CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>>) cacheBadgeAwardGenericEventServiceIF;
    this.relay = new Relay(relayUri);
    awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay);
    eventServiceIF.processIncomingEvent(new EventMessage(awardUpvoteDefinitionEvent));
  }

  @Test
  public void testValidateSaveMaterializedBadgeAwardGenericEventUpvote() {
    PublicKey upvotedUserPublicKey = Identity.generateRandomIdentity().getPublicKey();
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericVoteEvent = new BadgeAwardGenericEvent<>(
        identity,
        upvotedUserPublicKey,
        relay,
        awardUpvoteDefinitionEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardGenericVoteEvent));
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> dbMaterializedGenericAwardEvent =
        cacheBadgeAwardGenericEventServiceIF.materialize(badgeAwardGenericVoteEvent);
    assertEquals(badgeAwardGenericVoteEvent, dbMaterializedGenericAwardEvent);
  }

  @Test
  public void testValidateGetEventSaveBadgeAwardGenericEventUpvote() {
    PublicKey upvotedUserPublicKey = Identity.generateRandomIdentity().getPublicKey();
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericVoteEvent = new BadgeAwardGenericEvent<>(
        identity,
        upvotedUserPublicKey,
        relay,
        awardUpvoteDefinitionEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardGenericVoteEvent));

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> dbGetEventGenericAwardEvent = cacheBadgeAwardGenericEventServiceIF.getEvent(badgeAwardGenericVoteEvent.getId(), badgeAwardGenericVoteEvent.getRelayTagRelay().getUrl()).orElseThrow();
    assertEquals(badgeAwardGenericVoteEvent, dbGetEventGenericAwardEvent);
  }
}
