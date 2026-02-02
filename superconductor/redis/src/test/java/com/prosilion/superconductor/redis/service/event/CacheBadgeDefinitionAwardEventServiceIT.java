package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeDefinitionAwardEventService;
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
public class CacheBadgeDefinitionAwardEventServiceIT {
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);

  public final Identity authorIdentity = Identity.generateRandomIdentity();
  private final PublicKey reputationRecipientPublicKey = Identity.generateRandomIdentity().getPublicKey();

  private final BadgeDefinitionAwardEvent awardUpvoteDefinitionEvent;
  private final CacheBadgeDefinitionAwardEventService cacheBadgeDefinitionAwardEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  public CacheBadgeDefinitionAwardEventServiceIT(
      @Value("${superconductor.relay.url}") String relayUri,
      @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
      @NonNull @Qualifier("cacheBadgeDefinitionAwardEventService") CacheBadgeDefinitionAwardEventService cacheBadgeDefinitionAwardEventService) throws ParseException {
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeDefinitionAwardEventService = cacheBadgeDefinitionAwardEventService;
    this.relay = new Relay(relayUri);

    awardUpvoteDefinitionEvent = new BadgeDefinitionAwardEvent(authorIdentity, upvoteIdentifierTag, relay);
    eventServiceIF.processIncomingEvent(new EventMessage(awardUpvoteDefinitionEvent));
  }

  @Test
  public void testSaveBadgeDefinitionAwardEvent() {
    BadgeAwardGenericEvent<BadgeDefinitionAwardEvent> badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
        authorIdentity,
        reputationRecipientPublicKey,
        relay,
        awardUpvoteDefinitionEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardUpvoteEvent));
    BadgeDefinitionAwardEvent dbDefinitionAwardEvent = cacheBadgeDefinitionAwardEventService.getEvent(
        badgeAwardUpvoteEvent.getId(), relay.getUrl()).orElseThrow();

    assertEquals(badgeAwardUpvoteEvent.getBadgeDefinitionAwardEvent(), dbDefinitionAwardEvent);
    assertEquals(upvoteIdentifierTag, dbDefinitionAwardEvent.getIdentifierTag());

    String BADGE_DEFINITION_VOTE = "BADGE_DEFINITION_DOWNVOTE";
    IdentifierTag downvoteIdentifierTag = new IdentifierTag(BADGE_DEFINITION_VOTE);

    BadgeDefinitionAwardEvent awardDownvoteDefinitionEvent = new BadgeDefinitionAwardEvent(authorIdentity, downvoteIdentifierTag, relay);

    BadgeAwardGenericEvent<BadgeDefinitionAwardEvent> badgeAwardEventMinusOne = new BadgeAwardGenericEvent<>(
        authorIdentity,
        reputationRecipientPublicKey,
        relay,
        awardDownvoteDefinitionEvent);
  }
}
