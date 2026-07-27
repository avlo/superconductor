package com.prosilion.superconductor.redis.service.event;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheBadgeAwardGenericEventServiceIT extends BaseIntegrationTestFixtures {
  private final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent;
  private final CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService;

  private final EventServiceIF eventServiceIF;
  private final Relay relay;

  @Autowired
  public CacheBadgeAwardGenericEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUri,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService) {
    super(superconductorInstanceIdentity);
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeAwardGenericEventService = cacheBadgeAwardGenericEventService;
    this.relay = new Relay(relayUri);

    this.badgeDefinitionUpvoteEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       relay);
    cacheServiceIF.save(badgeDefinitionUpvoteEvent);
  }

  @Test
  public void testSaveBadgeAwardGenericEventUpvote() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       new BadgeAwardGenericEvent<>(
          parameterAimgIdentity,
          recipient.getPublicKey(),
          badgeDefinitionUpvoteEvent,
          relay);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardGenericEvent), relay);
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> dbGenericAwardEvent =
       cacheBadgeAwardGenericEventService
          .materialize(badgeAwardGenericEvent.asGenericEventRecord())
          .orElseThrow();

    assertEquals(badgeDefinitionUpvoteEvent, dbGenericAwardEvent.getBadgeDefinitionEvent());
  }

  @Test
  public void testGetByEventId() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       new BadgeAwardGenericEvent<>(
          parameterAimgIdentity,
          recipient.getPublicKey(),
          badgeDefinitionUpvoteEvent,
          relay);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardGenericEvent), relay);

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> actualAwardUpvoteDefinitionEvent =
       cacheBadgeAwardGenericEventService.getEvent(badgeAwardGenericEvent.getId(), relay);

    assertTrue(actualAwardUpvoteDefinitionEvent.isPresent());
  }
}
