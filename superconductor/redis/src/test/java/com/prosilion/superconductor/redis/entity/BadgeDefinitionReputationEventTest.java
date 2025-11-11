package com.prosilion.superconductor.redis.entity;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.prosilion.superconductor.redis.config.TestKindType.UNIT_REPUTATION;
import static com.prosilion.superconductor.redis.entity.FormulaEventTest.UNIT_DOWNVOTE;
import static com.prosilion.superconductor.redis.entity.FormulaEventTest.UNIT_UPVOTE;

public class BadgeDefinitionReputationEventTest {
  public static final Relay relay = new Relay("ws://localhost:5555");

  private static final String PLATFORM = BadgeDefinitionReputationEvent.class.getPackageName();
  private static final String IDENTITY = BadgeDefinitionReputationEvent.class.getSimpleName();
  private static final String PROOF = String.valueOf(BadgeDefinitionReputationEvent.class.hashCode());
  private final Identity identity = Identity.generateRandomIdentity();

  private final String PLUS_ONE = "+1";
  private final String MINUS_ONE = "-1";

  private final IdentifierTag upvoteIdTag = new IdentifierTag(UNIT_UPVOTE);
  private final IdentifierTag downvoteIdTag = new IdentifierTag(UNIT_DOWNVOTE);
  private final BadgeDefinitionAwardEvent upvoteDefnEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdTag, relay, PLUS_ONE);
  private final BadgeDefinitionAwardEvent downvoteDefnEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdTag, relay, MINUS_ONE);

  private final ExternalIdentityTag externalIdentityTag = new ExternalIdentityTag(PLATFORM, IDENTITY, PROOF);

  @Test
  void testUnmarshallUpvoteFormula() throws ParseException {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
        identity,
        new IdentifierTag(
            UNIT_REPUTATION.getName()),
        relay,
        externalIdentityTag,
        List.of(
            new FormulaEvent(
                identity,
                upvoteDefnEvent,
                PLUS_ONE)));
  }

  @Test
  void testUnmarshallUpvoteDownvoteFormula() throws ParseException {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
        identity,
        new IdentifierTag(
            UNIT_REPUTATION.getName()),
        relay,
        externalIdentityTag,
        List.of(
            new FormulaEvent(
                identity,
                upvoteDefnEvent,
                PLUS_ONE),
            new FormulaEvent(
                identity,
                downvoteDefnEvent,
                MINUS_ONE)));
  }
}
