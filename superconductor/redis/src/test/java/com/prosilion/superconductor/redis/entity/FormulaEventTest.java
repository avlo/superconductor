package com.prosilion.superconductor.redis.entity;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormulaEventTest {
  public static final Relay relay = new Relay("ws://localhost:5555");

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(BaseIntegrationTestFixtures.AWARD_UNIT_UPVOTE);
  public final IdentifierTag downvoteIdentifierTag = new IdentifierTag(BaseIntegrationTestFixtures.AWARD_UNIT_DOWNVOTE);

  public final Identity identity = Identity.generateRandomIdentity();
  public final PublicKey reputationDefinitionCreatorPublicKey = Identity.generateRandomIdentity().getPublicKey();

  public final String PLUS_ONE_FORMULA = "+1";
  public final String MINUS_ONE_FORMULA = "-1";

  final BadgeDefinitionGenericEvent awardUpvoteEvent = new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, PLUS_ONE_FORMULA, relay);
  final BadgeDefinitionGenericEvent awardDownvoteEvent = new BadgeDefinitionGenericEvent(identity, downvoteIdentifierTag, MINUS_ONE_FORMULA, relay);

  final FormulaEvent formulaEventUpvote;
  final FormulaEvent formulaEventDownvote;

  public FormulaEventTest() throws ParseException {
    this.formulaEventUpvote = new FormulaEvent(identity, upvoteIdentifierTag, awardUpvoteEvent, PLUS_ONE_FORMULA, relay);
    this.formulaEventDownvote = new FormulaEvent(identity, downvoteIdentifierTag, awardDownvoteEvent, MINUS_ONE_FORMULA, relay);
  }

  @Test
  void equalityTest() throws ParseException {
    assertNotEquals(awardUpvoteEvent, new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, PLUS_ONE_FORMULA, relay));
    assertNotEquals(awardDownvoteEvent, new BadgeDefinitionGenericEvent(identity, downvoteIdentifierTag, MINUS_ONE_FORMULA, relay));
    assertNotEquals(formulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, awardUpvoteEvent, PLUS_ONE_FORMULA, relay));
    assertNotEquals(formulaEventDownvote, new FormulaEvent(identity, downvoteIdentifierTag, awardDownvoteEvent, MINUS_ONE_FORMULA, relay));

    assertNotEquals(awardUpvoteEvent, awardDownvoteEvent);
    assertNotEquals(formulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, awardUpvoteEvent, MINUS_ONE_FORMULA, relay));

    BadgeDefinitionGenericEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionGenericEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, PLUS_ONE_FORMULA, relay);
    assertNotEquals(awardUpvoteEvent, awardUpvoteEventDifferentIdentity);
    assertNotEquals(awardUpvoteEventDifferentIdentity, awardUpvoteEvent);

    FormulaEvent formulaEventUpvoteDtoDifferentIdentity = new FormulaEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, awardUpvoteEvent, PLUS_ONE_FORMULA, relay);
    assertNotEquals(formulaEventUpvote, formulaEventUpvoteDtoDifferentIdentity);
    assertNotEquals(formulaEventUpvoteDtoDifferentIdentity, formulaEventUpvote);
  }

  @Test
  void testGenericEventRecordFormulaEventCreation() throws ParseException {
    FormulaEvent expected = new FormulaEvent(
       identity,
       upvoteIdentifierTag,
       awardUpvoteEvent,
       "+1",
       relay);

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
          NostrException.class, () ->
             new FormulaEvent(identity, upvoteIdentifierTag, blankFormulaAwardEvent, "", relay))
          .getMessage().contains("supplied formula is blank"));
  }

  @Test
  void testDifferentContent() {
    BadgeDefinitionGenericEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, "+2", relay);
    assertNotEquals(awardUpvoteEvent, awardUpvoteEventDifferentIdentity);
  }

  @Test
  void testDifferentContentDto() throws ParseException {
    BadgeDefinitionGenericEvent differentContentDto = new BadgeDefinitionGenericEvent(
       identity, upvoteIdentifierTag, BaseIntegrationTestFixtures.AWARD_UNIT_UPVOTE, relay);

    assertNotEquals(formulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, differentContentDto, "+2", relay));
  }

  @Test
  void formulaContentTest() throws ParseException {
    assertEquals(
       "BadgeDefinitionReputationEvent FormulaEvent(s) operator(s) default content: BADGE_DEFN_UNIT_REP == (previous)BADGE_DEFN_UNIT_REP +1(BDG_DEF_UNIT_UP) -1(BDG_DEF_UNIT_DOWN)",
       new BadgeDefinitionReputationEvent(
          identity,
          reputationDefinitionCreatorPublicKey,
          new IdentifierTag(
             BaseIntegrationTestFixtures.TEST_UNIT_REPUTATION),
          relay,
          BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
          List.of(
             formulaEventUpvote,
             formulaEventDownvote)).getContent());

    String UNIT_UPVOTE_UNIQUE = "UNIT_UPVOTE_UNIQUE";
    String UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA = "+1";
    IdentifierTag upvoteUniqueIdentifierTag = new IdentifierTag(UNIT_UPVOTE_UNIQUE);
    BadgeDefinitionGenericEvent awardUniqueUpvoteEvent = new BadgeDefinitionGenericEvent(identity, upvoteUniqueIdentifierTag, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA, relay);

    assertEquals(
       "BadgeDefinitionReputationEvent FormulaEvent(s) operator(s) default content: BADGE_DEFN_UNIT_REP == (previous)BADGE_DEFN_UNIT_REP +1(BDG_DEF_UNIT_UP) +1(UNIT_UPVOTE_UNIQUE)",
       new BadgeDefinitionReputationEvent(
          identity,
          reputationDefinitionCreatorPublicKey,
          new IdentifierTag(
             BaseIntegrationTestFixtures.TEST_UNIT_REPUTATION),
          relay,
          BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
          List.of(
             formulaEventUpvote,
             new FormulaEvent(
                identity, upvoteIdentifierTag, awardUniqueUpvoteEvent, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA, relay))).getContent());
  }
}
