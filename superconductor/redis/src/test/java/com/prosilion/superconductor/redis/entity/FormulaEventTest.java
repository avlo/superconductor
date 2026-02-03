package com.prosilion.superconductor.redis.entity;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_DOWNVOTE;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_REPUTATION;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormulaEventTest {
  public static final Relay relay = new Relay("ws://localhost:5555");

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);
  public final IdentifierTag downvoteIdentifierTag = new IdentifierTag(TEST_UNIT_DOWNVOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  public final String PLUS_ONE_FORMULA = "+1";
  public final String MINUS_ONE_FORMULA = "-1";

  final BadgeDefinitionGenericEvent awardUpvoteEvent = new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
  final BadgeDefinitionGenericEvent awardDownvoteEvent = new BadgeDefinitionGenericEvent(identity, downvoteIdentifierTag, relay, MINUS_ONE_FORMULA);

  final FormulaEvent formulaEventUpvote;
  final FormulaEvent formulaEventDownvote;

  public FormulaEventTest() throws ParseException {
    this.formulaEventUpvote = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteEvent, PLUS_ONE_FORMULA);
    this.formulaEventDownvote = new FormulaEvent(identity, downvoteIdentifierTag, relay, awardDownvoteEvent, MINUS_ONE_FORMULA);
  }

  @Test
  void equalityTest() throws ParseException {
    assertNotEquals(awardUpvoteEvent, new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA));
    assertNotEquals(awardDownvoteEvent, new BadgeDefinitionGenericEvent(identity, downvoteIdentifierTag, relay, MINUS_ONE_FORMULA));
    assertNotEquals(formulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteEvent, PLUS_ONE_FORMULA));
    assertNotEquals(formulaEventDownvote, new FormulaEvent(identity, downvoteIdentifierTag, relay, awardDownvoteEvent, MINUS_ONE_FORMULA));

    assertNotEquals(awardUpvoteEvent, awardDownvoteEvent);
    assertNotEquals(formulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteEvent, MINUS_ONE_FORMULA));

    BadgeDefinitionGenericEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionGenericEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
    assertNotEquals(awardUpvoteEvent, awardUpvoteEventDifferentIdentity);
    assertNotEquals(awardUpvoteEventDifferentIdentity, awardUpvoteEvent);

    FormulaEvent formulaEventUpvoteDtoDifferentIdentity = new FormulaEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, relay, awardUpvoteEvent, PLUS_ONE_FORMULA);
    assertNotEquals(formulaEventUpvote, formulaEventUpvoteDtoDifferentIdentity);
    assertNotEquals(formulaEventUpvoteDtoDifferentIdentity, formulaEventUpvote);
  }

  @Test
  void testGenericEventRecordFormulaEventCreation() throws ParseException {
    FormulaEvent expected = new FormulaEvent(
        identity,
        upvoteIdentifierTag,
        relay,
        awardUpvoteEvent,
        "+1");

    Function<AddressTag, BadgeDefinitionGenericEvent> fxn = addressTag ->
        awardUpvoteEvent;

    assertEquals(
        expected.getBadgeDefinitionGenericEvent(),
        new FormulaEvent(
            expected.getGenericEventRecord(),
            fxn).getBadgeDefinitionGenericEvent());
  }

  @Test
  void testBlankFormula() {
    BadgeDefinitionGenericEvent blankFormulaAwardEvent = new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay);

    assertTrue(
        assertThrows(
            ParseException.class, () ->
                new FormulaEvent(identity, upvoteIdentifierTag, relay, blankFormulaAwardEvent, ""))
            .getMessage().contains("supplied formula is blank"));
  }

  @Test
  void testDifferentContent() {
    BadgeDefinitionGenericEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay, "+2");
    assertNotEquals(awardUpvoteEvent, awardUpvoteEventDifferentIdentity);
  }

  @Test
  void testDifferentContentDto() throws ParseException {
    BadgeDefinitionGenericEvent differentContentDto = new BadgeDefinitionGenericEvent(
        identity, upvoteIdentifierTag, relay, TEST_UNIT_UPVOTE);

    assertNotEquals(formulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, relay, differentContentDto, "+2"));
  }

  @Test
  void formulaContentTest() throws ParseException {
    assertEquals("TEST_UNIT_REPUTATION == (previous)TEST_UNIT_REPUTATION +1(TEST_UNIT_UPVOTE) -1(TEST_UNIT_DOWNVOTE)",
        new BadgeDefinitionReputationEvent(
            identity,
            new IdentifierTag(
                TEST_UNIT_REPUTATION),
            relay,
            BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
            List.of(
                formulaEventUpvote,
                formulaEventDownvote)).getContent());

    String UNIT_UPVOTE_UNIQUE = "UNIT_UPVOTE_UNIQUE";
    String UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA = "+1";
    IdentifierTag upvoteUniqueIdentifierTag = new IdentifierTag(UNIT_UPVOTE_UNIQUE);
    BadgeDefinitionGenericEvent awardUniqueUpvoteEvent = new BadgeDefinitionGenericEvent(identity, upvoteUniqueIdentifierTag, relay, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA);

    assertEquals("TEST_UNIT_REPUTATION == (previous)TEST_UNIT_REPUTATION +1(TEST_UNIT_UPVOTE) +1(UNIT_UPVOTE_UNIQUE)",
        new BadgeDefinitionReputationEvent(
            identity,
            new IdentifierTag(
                TEST_UNIT_REPUTATION),
            relay,
            BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
            List.of(
                formulaEventUpvote,
                new FormulaEvent(
                    identity, upvoteIdentifierTag, relay, awardUniqueUpvoteEvent, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA))).getContent());
  }
}
