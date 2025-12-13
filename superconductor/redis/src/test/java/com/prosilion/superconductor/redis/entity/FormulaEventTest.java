package com.prosilion.superconductor.redis.entity;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormulaEventTest {
  public static final Relay relay = new Relay("ws://localhost:5555");

  public static final String UNIT_REPUTATION = "UNIT_REPUTATION";
  public static final String UNIT_UPVOTE = "UNIT_UPVOTE";
  public static final String UNIT_DOWNVOTE = "UNIT_DOWNVOTE";

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(UNIT_UPVOTE);
  public final IdentifierTag downvoteIdentifierTag = new IdentifierTag(UNIT_DOWNVOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  public final String PLUS_ONE_FORMULA = "+1";
  public final String MINUS_ONE_FORMULA = "-1";

  final BadgeDefinitionAwardEvent awardUpvoteEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
  final BadgeDefinitionAwardEvent awardDownvoteEvent = new BadgeDefinitionAwardEvent(identity, downvoteIdentifierTag, relay, MINUS_ONE_FORMULA);

  final FormulaEvent formulaEventUpvote;
  final FormulaEvent formulaEventDownvote;

  public static final String PLATFORM = FormulaEventTest.class.getPackageName();
  public static final String IDENTITY = FormulaEventTest.class.getSimpleName();
  public static final String PROOF = String.valueOf(FormulaEventTest.class.hashCode());

  ExternalIdentityTag externalIdentityTag;

  public FormulaEventTest() throws ParseException {
    this.formulaEventUpvote = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteEvent, PLUS_ONE_FORMULA);
    this.formulaEventDownvote = new FormulaEvent(identity, downvoteIdentifierTag, relay, awardDownvoteEvent, MINUS_ONE_FORMULA);
    this.externalIdentityTag = new ExternalIdentityTag(PLATFORM, IDENTITY, PROOF);
  }

  @Test
  void equalityTest() throws ParseException {
    assertNotEquals(awardUpvoteEvent, new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA));
    assertNotEquals(awardDownvoteEvent, new BadgeDefinitionAwardEvent(identity, downvoteIdentifierTag, relay, MINUS_ONE_FORMULA));
    assertNotEquals(formulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteEvent, PLUS_ONE_FORMULA));
    assertNotEquals(formulaEventDownvote, new FormulaEvent(identity, downvoteIdentifierTag, relay, awardDownvoteEvent, MINUS_ONE_FORMULA));

    assertNotEquals(awardUpvoteEvent, awardDownvoteEvent);
    assertNotEquals(formulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteEvent, MINUS_ONE_FORMULA));

    BadgeDefinitionAwardEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionAwardEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
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

    Function<AddressTag, BadgeDefinitionAwardEvent> fxn = addressTag ->
        awardUpvoteEvent;

    assertEquals(
        expected.getBadgeDefinitionAwardEvent(),
        new FormulaEvent(
            expected.getGenericEventRecord(),
            fxn).getBadgeDefinitionAwardEvent());
  }

  @Test
  void testBlankFormula() {
    BadgeDefinitionAwardEvent blankFormulaAwardEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay);

    assertTrue(
        assertThrows(
            ParseException.class, () ->
                new FormulaEvent(identity, upvoteIdentifierTag, relay, blankFormulaAwardEvent, ""))
            .getMessage().contains("supplied formula is blank"));
  }

  @Test
  void testDifferentContent() {
    BadgeDefinitionAwardEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay, "+2");
    assertNotEquals(awardUpvoteEvent, awardUpvoteEventDifferentIdentity);
  }

  @Test
  void testDifferentContentDto() throws ParseException {
    BadgeDefinitionAwardEvent differentContentDto = new BadgeDefinitionAwardEvent(
        identity, upvoteIdentifierTag, relay, UNIT_UPVOTE);

    assertNotEquals(formulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, relay, differentContentDto, "+2"));
  }

  @Test
  void formulaContentTest() throws ParseException {
    assertEquals("UNIT_REPUTATION == (previous)UNIT_REPUTATION +1(UNIT_UPVOTE) -1(UNIT_DOWNVOTE)",
        new BadgeDefinitionReputationEvent(
            identity,
            new IdentifierTag(
                UNIT_REPUTATION),
            relay,
            externalIdentityTag,
            List.of(
                formulaEventUpvote,
                formulaEventDownvote)).getContent());

    String UNIT_UPVOTE_UNIQUE = "UNIT_UPVOTE_UNIQUE";
    String UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA = "+1";
    IdentifierTag upvoteUniqueIdentifierTag = new IdentifierTag(UNIT_UPVOTE_UNIQUE);
    BadgeDefinitionAwardEvent awardUniqueUpvoteEvent = new BadgeDefinitionAwardEvent(identity, upvoteUniqueIdentifierTag, relay, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA);

    assertEquals("UNIT_REPUTATION == (previous)UNIT_REPUTATION +1(UNIT_UPVOTE) +1(UNIT_UPVOTE_UNIQUE)",
        new BadgeDefinitionReputationEvent(
            identity,
            new IdentifierTag(
                UNIT_REPUTATION),
            relay,
            externalIdentityTag,
            List.of(
                formulaEventUpvote,
                new FormulaEvent(
                    identity, upvoteIdentifierTag, relay, awardUniqueUpvoteEvent, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA))).getContent());
  }
}
