package com.prosilion.superconductor.redis.entity;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.dto.BadgeDefinitionAwardEventDto;
import com.prosilion.superconductor.base.dto.BadgeDefinitionReputationEventVariantDto;
import com.prosilion.superconductor.base.dto.FormulaEventDtoVariantWithFormulaEvent;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormulaEventDtoVariantTest {
  public final static String UNIT_REPUTATION = "UNIT_REPUTATION";
  public final static String UNIT_UPVOTE = "UNIT_UPVOTE";
  public final static String UNIT_DOWNVOTE = "UNIT_DOWNVOTE";

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(UNIT_UPVOTE);
  public final IdentifierTag downvoteIdentifierTag = new IdentifierTag(UNIT_DOWNVOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  public final String PLUS_ONE_FORMULA = "+1";
  public final String MINUS_ONE_FORMULA = "-1";

  final BadgeDefinitionAwardEvent awardUpvoteEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, PLUS_ONE_FORMULA);
  final BadgeDefinitionAwardEvent awardDownvoteEvent = new BadgeDefinitionAwardEvent(identity, downvoteIdentifierTag, MINUS_ONE_FORMULA);

  final FormulaEvent upvoteFormulaEvent = new FormulaEvent(identity, upvoteIdentifierTag, PLUS_ONE_FORMULA);
  final FormulaEvent downvoteFormulaEvent = new FormulaEvent(identity, downvoteIdentifierTag, MINUS_ONE_FORMULA);

  final FormulaEventDtoVariantWithFormulaEvent formulaEventDtoVariantWithUpvote;
  final FormulaEventDtoVariantWithFormulaEvent formulaEventDtoVariantWithDownvote;

  public FormulaEventDtoVariantTest() throws ParseException {
    this.formulaEventDtoVariantWithUpvote = new FormulaEventDtoVariantWithFormulaEvent(upvoteFormulaEvent);
    this.formulaEventDtoVariantWithDownvote = new FormulaEventDtoVariantWithFormulaEvent(downvoteFormulaEvent);
  }

  @Test
  void equalityTest() throws ParseException {
    assertEquals(awardUpvoteEvent, new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, PLUS_ONE_FORMULA));
    assertEquals(awardDownvoteEvent, new BadgeDefinitionAwardEvent(identity, downvoteIdentifierTag, MINUS_ONE_FORMULA));
    assertEquals(formulaEventDtoVariantWithUpvote, new FormulaEventDtoVariantWithFormulaEvent(upvoteFormulaEvent));
    assertEquals(formulaEventDtoVariantWithDownvote, new FormulaEventDtoVariantWithFormulaEvent(downvoteFormulaEvent));

    assertNotEquals(awardUpvoteEvent, awardDownvoteEvent);
    assertNotEquals(formulaEventDtoVariantWithUpvote, new FormulaEventDtoVariantWithFormulaEvent(downvoteFormulaEvent));

    FormulaEventDtoVariantWithFormulaEvent formulaEventUpvoteDtoDifferentIdentity = new FormulaEventDtoVariantWithFormulaEvent(new FormulaEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, PLUS_ONE_FORMULA));
    assertNotEquals(formulaEventDtoVariantWithUpvote, formulaEventUpvoteDtoDifferentIdentity);
    assertNotEquals(formulaEventUpvoteDtoDifferentIdentity, formulaEventDtoVariantWithUpvote);
  }

  @Test
  void testBlankFormula() {
    assertTrue(
        assertThrows(
            ParseException.class, () ->
                new FormulaEventDtoVariantWithFormulaEvent(
                    new FormulaEvent(identity, upvoteIdentifierTag, "")))
            .getMessage().contains("supplied formula is blank"));
  }

  @Test
  void testFormulaEventAssignable() throws ParseException {
    assertEquals(formulaEventDtoVariantWithUpvote, new FormulaEventDtoVariantWithFormulaEvent(upvoteFormulaEvent));
  }

  @Test
  void testAssignable() {
    BadgeDefinitionAwardEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionAwardEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, PLUS_ONE_FORMULA);
    assertNotEquals(awardUpvoteEvent, awardUpvoteEventDifferentIdentity);
    assertNotEquals(awardUpvoteEventDifferentIdentity, awardUpvoteEvent);

    BadgeDefinitionAwardEventDto badgeDefinitionAwardUpvoteEventDtoDifferentIdentity = new BadgeDefinitionAwardEventDto(awardUpvoteEventDifferentIdentity);

    assertNotEquals(formulaEventDtoVariantWithUpvote, badgeDefinitionAwardUpvoteEventDtoDifferentIdentity);
  }

  @Test
  void testDifferentContent() {
    BadgeDefinitionAwardEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, "+2");
    assertNotEquals(awardUpvoteEvent, awardUpvoteEventDifferentIdentity);
  }

  @Test
  void testDifferentContentDto() throws ParseException {
    FormulaEvent differentContentDto = new FormulaEvent(identity, upvoteIdentifierTag, "+2");
    assertNotEquals(formulaEventDtoVariantWithUpvote, new FormulaEventDtoVariantWithFormulaEvent(differentContentDto));
  }

  @Test
  void consoleLogTest() throws ParseException {
    assertEquals("UNIT_REPUTATION == (previous)UNIT_REPUTATION +1(UNIT_UPVOTE) -1(UNIT_DOWNVOTE)",
        new BadgeDefinitionReputationEventVariantDto(
            identity,
            new IdentifierTag(
                UNIT_REPUTATION),
            List.of(
                formulaEventDtoVariantWithUpvote,
                formulaEventDtoVariantWithDownvote))
            .getBadgeDefinitionAwardEvent().getContent());

    String UNIT_UPVOTE_UNIQUE = "UNIT_UPVOTE_UNIQUE";
    String UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA = "+1";
    IdentifierTag upvoteUniqueIdentifierTag = new IdentifierTag(UNIT_UPVOTE_UNIQUE);

    assertEquals("UNIT_REPUTATION == (previous)UNIT_REPUTATION +1(UNIT_UPVOTE) +1(UNIT_UPVOTE_UNIQUE)",
        new BadgeDefinitionReputationEventVariantDto(
            identity,
            new IdentifierTag(
                UNIT_REPUTATION),
            List.of(
                formulaEventDtoVariantWithUpvote,
                new FormulaEventDtoVariantWithFormulaEvent(
                    new FormulaEvent(identity, upvoteUniqueIdentifierTag, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA))))
            .getBadgeDefinitionAwardEvent().getContent());

    assertThrows(NostrException.class, () ->
        new BadgeDefinitionReputationEventVariantDto(
            identity,
            new IdentifierTag(
                UNIT_REPUTATION),
            List.of(
                formulaEventDtoVariantWithUpvote,
                formulaEventDtoVariantWithUpvote)));
  }
}
