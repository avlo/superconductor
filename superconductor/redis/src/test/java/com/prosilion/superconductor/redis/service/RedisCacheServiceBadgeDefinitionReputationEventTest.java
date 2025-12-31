package com.prosilion.superconductor.redis.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_AWARD_REPUTATION;
import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_DOWNVOTE;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
public class RedisCacheServiceBadgeDefinitionReputationEventTest {
  public static final Relay relay = new Relay("ws://localhost:5555");
  private final Identity identity = Identity.generateRandomIdentity();

  private final String PLUS_ONE = "+1";
  private final String MINUS_ONE = "-1";

  private final IdentifierTag upvoteIdTag = new IdentifierTag(TEST_UNIT_UPVOTE);
  private final IdentifierTag downvoteIdTag = new IdentifierTag(TEST_UNIT_DOWNVOTE);
  private final BadgeDefinitionAwardEvent upvoteDefnEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdTag, relay, PLUS_ONE);
  private final BadgeDefinitionAwardEvent downvoteDefnEvent = new BadgeDefinitionAwardEvent(identity, downvoteIdTag, relay, MINUS_ONE);

  private final CacheServiceIF redisCacheServiceIF;

  @Autowired
  public RedisCacheServiceBadgeDefinitionReputationEventTest(CacheServiceIF cacheServiceIF) {
    this.redisCacheServiceIF = cacheServiceIF;
  }

  @Test
  void testUnmarshallUpvoteFormula() throws ParseException {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
        identity,
        new IdentifierTag(
            BADGE_AWARD_REPUTATION.getName()),
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        List.of(
            new FormulaEvent(
                identity,
                upvoteIdTag,
                relay,
                upvoteDefnEvent,
                PLUS_ONE)));
  }

  @Test
  void testMissingFormulaEvent() throws ParseException {

  }

  @Test
  void testUnmarshallUpvoteDownvoteFormula() throws ParseException {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
        identity,
        new IdentifierTag(
            BADGE_AWARD_REPUTATION.getName()),
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        List.of(
            new FormulaEvent(
                identity,
                upvoteIdTag,
                relay,
                upvoteDefnEvent,
                PLUS_ONE),
            new FormulaEvent(
                identity,
                downvoteIdTag,
                relay,
                downvoteDefnEvent,
                MINUS_ONE)));
  }
}
