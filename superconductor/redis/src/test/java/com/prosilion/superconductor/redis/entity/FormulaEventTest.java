package com.prosilion.superconductor.redis.entity;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.dto.BadgeDefinitionAwardEventDto;
import com.prosilion.superconductor.base.dto.BadgeDefinitionReputationEventDto;
import com.prosilion.superconductor.base.dto.FormulaEventDto;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormulaEventTest {
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

  final BadgeDefinitionAwardEventDto badgeDefinitionAwardUpvoteEventDto = new BadgeDefinitionAwardEventDto(awardUpvoteEvent);
  final BadgeDefinitionAwardEventDto badgeDefinitionAwardDownvoteEventDto = new BadgeDefinitionAwardEventDto(awardDownvoteEvent);

  final FormulaEventDto formulaEventUpvoteDto;
  final FormulaEventDto formulaEventDownvoteDto;

  public FormulaEventTest() throws ParseException {
    this.formulaEventUpvoteDto = new FormulaEventDto(badgeDefinitionAwardUpvoteEventDto);
    this.formulaEventDownvoteDto = new FormulaEventDto(badgeDefinitionAwardDownvoteEventDto);
  }

  @Test
  void equalityTest() throws ParseException {
    assertEquals(awardUpvoteEvent, new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, PLUS_ONE_FORMULA));
    assertEquals(awardDownvoteEvent, new BadgeDefinitionAwardEvent(identity, downvoteIdentifierTag, MINUS_ONE_FORMULA));
    assertEquals(badgeDefinitionAwardUpvoteEventDto, new BadgeDefinitionAwardEventDto(awardUpvoteEvent));
    assertEquals(badgeDefinitionAwardDownvoteEventDto, new BadgeDefinitionAwardEventDto(awardDownvoteEvent));
    assertEquals(formulaEventUpvoteDto, new FormulaEventDto(badgeDefinitionAwardUpvoteEventDto));
    assertEquals(formulaEventDownvoteDto, new FormulaEventDto(badgeDefinitionAwardDownvoteEventDto));

    assertNotEquals(awardUpvoteEvent, awardDownvoteEvent);
    assertNotEquals(badgeDefinitionAwardUpvoteEventDto, badgeDefinitionAwardDownvoteEventDto);
    assertNotEquals(formulaEventUpvoteDto, new FormulaEventDto(badgeDefinitionAwardDownvoteEventDto));

    BadgeDefinitionAwardEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionAwardEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, PLUS_ONE_FORMULA);
    assertNotEquals(awardUpvoteEvent, awardUpvoteEventDifferentIdentity);

    BadgeDefinitionAwardEventDto badgeDefinitionAwardUpvoteEventDtoDifferentIdentity = new BadgeDefinitionAwardEventDto(awardUpvoteEventDifferentIdentity);
    assertNotEquals(badgeDefinitionAwardUpvoteEventDto, badgeDefinitionAwardUpvoteEventDtoDifferentIdentity);

    FormulaEventDto formulaEventUpvoteDtoDifferentIdentity = new FormulaEventDto(badgeDefinitionAwardUpvoteEventDtoDifferentIdentity);
    assertNotEquals(formulaEventUpvoteDto, formulaEventUpvoteDtoDifferentIdentity);
  }

  @Test
  void consoleLogTest() throws ParseException {
    assertEquals("UNIT_REPUTATION == (previous)UNIT_REPUTATION +1(UNIT_UPVOTE) -1(UNIT_DOWNVOTE)",
        new BadgeDefinitionReputationEventDto(
            identity,
            new IdentifierTag(
                UNIT_REPUTATION),
            List.of(
                formulaEventUpvoteDto,
                formulaEventDownvoteDto))
            .getBadgeDefinitionAwardEvent().getContent());

    String UNIT_UPVOTE_UNIQUE = "UNIT_UPVOTE_UNIQUE";
    String UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA = "+1";
    IdentifierTag upvoteUniqueIdentifierTag = new IdentifierTag(UNIT_UPVOTE_UNIQUE);
    BadgeDefinitionAwardEvent awardUniqueUpvoteEvent = new BadgeDefinitionAwardEvent(identity, upvoteUniqueIdentifierTag, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA);

    assertEquals("UNIT_REPUTATION == (previous)UNIT_REPUTATION +1(UNIT_UPVOTE) +1(UNIT_UPVOTE_UNIQUE)",
        new BadgeDefinitionReputationEventDto(
            identity,
            new IdentifierTag(
                UNIT_REPUTATION),
            List.of(
                formulaEventUpvoteDto,
                new FormulaEventDto(
                    new BadgeDefinitionAwardEventDto(
                        awardUniqueUpvoteEvent))))
            .getBadgeDefinitionAwardEvent().getContent());

    assertThrows(NostrException.class, () ->
        new BadgeDefinitionReputationEventDto(
            identity,
            new IdentifierTag(
                UNIT_REPUTATION),
            List.of(
                formulaEventUpvoteDto,
                formulaEventUpvoteDto)));
  }
}
