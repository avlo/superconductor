package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
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
public class CacheBadgeDefinitionGenericEventServiceIT {
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);

  public final Identity authorIdentity = Identity.generateRandomIdentity();
  private final PublicKey reputationRecipientPublicKey = Identity.generateRandomIdentity().getPublicKey();

  private final BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;
  private final CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  public CacheBadgeDefinitionGenericEventServiceIT(
      @Value("${superconductor.relay.url}") String relayUri,
      @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
      @NonNull @Qualifier("cacheBadgeDefinitionGenericEventService") CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) throws ParseException {
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeDefinitionGenericEventService = cacheBadgeDefinitionGenericEventService;
    this.relay = new Relay(relayUri);

    awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(authorIdentity, upvoteIdentifierTag, relay);
    eventServiceIF.processIncomingEvent(new EventMessage(awardUpvoteDefinitionEvent));
  }

  @Test
  public void testSaveBadgeDefinitionGenericEvent() throws JsonProcessingException {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
        authorIdentity,
        reputationRecipientPublicKey,
        relay,
        awardUpvoteDefinitionEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardUpvoteEvent));
    BadgeDefinitionGenericEvent dbDefinitionGenericEvent = cacheBadgeDefinitionGenericEventService.getAddressTagEvent(
        badgeAwardUpvoteEvent.getAddressTag()).orElseThrow();

    assertEquals(badgeAwardUpvoteEvent.getBadgeDefinitionGenericEvent(), dbDefinitionGenericEvent);
    assertEquals(upvoteIdentifierTag, dbDefinitionGenericEvent.getIdentifierTag());

    String BADGE_DEFINITION_VOTE = "BADGE_DEFINITION_DOWNVOTE";
    IdentifierTag downvoteIdentifierTag = new IdentifierTag(BADGE_DEFINITION_VOTE);

    BadgeDefinitionGenericEvent awardDownvoteDefinitionEvent = new BadgeDefinitionGenericEvent(authorIdentity, downvoteIdentifierTag, relay);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEventMinusOne = new BadgeAwardGenericEvent<>(
        authorIdentity,
        reputationRecipientPublicKey,
        relay,
        awardDownvoteDefinitionEvent);
  }
}
