package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
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
  public static final Relay relay = new Relay("ws://localhost:5555");
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);
  public final Identity identity = Identity.generateRandomIdentity();

  private final BadgeDefinitionAwardEvent awardUpvoteDefinitionEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay);

  private final EventServiceIF eventServiceIF;
  private final CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF;

  public CacheBadgeAwardGenericEventServiceIT(
      @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF) throws ParseException {
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeAwardGenericEventServiceIF = cacheBadgeAwardGenericEventServiceIF;

    eventServiceIF.processIncomingEvent(new EventMessage(awardUpvoteDefinitionEvent));
  }

  @Test
  public void testSaveBadgeAwardGenericEventUpvote() {
    PublicKey upvotedUserPublicKey = Identity.generateRandomIdentity().getPublicKey();
    BadgeAwardGenericEvent badgeAwardGenericVoteEvent = new BadgeAwardGenericEvent(
        identity,
        upvotedUserPublicKey,
        awardUpvoteDefinitionEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardGenericVoteEvent));
    BadgeAwardGenericEvent dbGenericAwardEvent = cacheBadgeAwardGenericEventServiceIF.getEvent(badgeAwardGenericVoteEvent.getId()).orElseThrow();
    assertEquals(badgeAwardGenericVoteEvent, dbGenericAwardEvent);
  }
}
