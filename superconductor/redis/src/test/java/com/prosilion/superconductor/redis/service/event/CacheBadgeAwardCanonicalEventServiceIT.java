package com.prosilion.superconductor.redis.service.event;

import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardCanonicalEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestDirtiesContextFixtures;
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
public class CacheBadgeAwardCanonicalEventServiceIT extends BaseIntegrationTestDirtiesContextFixtures {
  private final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent;
  private final CacheBadgeAwardCanonicalEventService cacheBadgeAwardCanonicalEventService;

  private final EventServiceIF eventServiceIF;
  private final Relay relay;

  @Autowired
  public CacheBadgeAwardCanonicalEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUri,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheBadgeAwardCanonicalEventService") CacheBadgeAwardCanonicalEventService cacheBadgeAwardCanonicalEventService) {
    super(superconductorInstanceIdentity);
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeAwardCanonicalEventService = cacheBadgeAwardCanonicalEventService;
    this.relay = new Relay(relayUri);

    this.badgeDefinitionUpvoteEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       relay);
    cacheServiceIF.save(badgeDefinitionUpvoteEvent);
  }

  @Test
  public void testSaveBadgeAwardCanonicalEventUpvote() {
    BadgeAwardCanonicalEvent badgeAwardCanonicalEvent =
       new BadgeAwardCanonicalEvent(
          superconductorInstanceIdentity,
          recipient.getPublicKey(),
          badgeDefinitionUpvoteEvent,
          relay);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardCanonicalEvent), relay);
    BadgeAwardCanonicalEvent dbGenericAwardEvent =
       cacheBadgeAwardCanonicalEventService
          .materialize(badgeAwardCanonicalEvent.asGenericEventRecord())
          .orElseThrow();

    assertEquals(badgeDefinitionUpvoteEvent, dbGenericAwardEvent.getBadgeDefinitionEvent());
  }

  @Test
  public void testGetByEventId() {
    BadgeAwardCanonicalEvent badgeAwardCanonicalEvent =
       new BadgeAwardCanonicalEvent(
          superconductorInstanceIdentity,
          recipient.getPublicKey(),
          badgeDefinitionUpvoteEvent,
          relay);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardCanonicalEvent), relay);

    Optional<BadgeAwardCanonicalEvent> actualAwardUpvoteDefinitionEvent =
       cacheBadgeAwardCanonicalEventService.getEvent(badgeAwardCanonicalEvent.getId(), relay);

    assertTrue(actualAwardUpvoteDefinitionEvent.isPresent());
  }

  @Test
  public void testGetByDirectAddressTag() {
    BadgeAwardCanonicalEvent badgeAwardCanonicalEvent =
       new BadgeAwardCanonicalEvent(
          superconductorInstanceIdentity,
          recipient.getPublicKey(),
          badgeDefinitionUpvoteEvent,
          relay);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardCanonicalEvent), relay);

    Optional<BadgeAwardCanonicalEvent> actualAwardUpvoteDefinitionEvent =
       cacheBadgeAwardCanonicalEventService.getByDirect(badgeDefinitionUpvoteEvent.asAddressableEventAddressTag());

    assertTrue(actualAwardUpvoteDefinitionEvent.isPresent());
  }
}
