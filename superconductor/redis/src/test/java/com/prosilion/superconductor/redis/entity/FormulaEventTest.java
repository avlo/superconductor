package com.prosilion.superconductor.redis.entity;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.aImgIdentity;
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

  final CuratedFormulaEvent curatedFormulaEventUpvote;
  final CuratedFormulaEvent curatedFormulaEventDownvote;

  public FormulaEventTest() {
    this.curatedFormulaEventUpvote = new CuratedFormulaEvent(aImgIdentity,
       new FormulaEvent(identity, upvoteIdentifierTag, awardUpvoteEvent, PLUS_ONE_FORMULA, relay),
       new ReferenceTag(relay.getUrl()),
       relay);

    this.curatedFormulaEventDownvote = new CuratedFormulaEvent(aImgIdentity,
       new FormulaEvent(identity, downvoteIdentifierTag, awardDownvoteEvent, MINUS_ONE_FORMULA, relay),
       new ReferenceTag(relay.getUrl()),
       relay);
  }

  @Test
  void equalityTest() {
    assertNotEquals(awardUpvoteEvent, new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, PLUS_ONE_FORMULA, relay));
    assertNotEquals(awardDownvoteEvent, new BadgeDefinitionGenericEvent(identity, downvoteIdentifierTag, MINUS_ONE_FORMULA, relay));
    assertNotEquals(curatedFormulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, awardUpvoteEvent, PLUS_ONE_FORMULA, relay));
    assertNotEquals(curatedFormulaEventDownvote, new FormulaEvent(identity, downvoteIdentifierTag, awardDownvoteEvent, MINUS_ONE_FORMULA, relay));

    assertNotEquals(awardUpvoteEvent, awardDownvoteEvent);
    assertNotEquals(curatedFormulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, awardUpvoteEvent, MINUS_ONE_FORMULA, relay));

    BadgeDefinitionGenericEvent awardUpvoteEventDifferentIdentity = new BadgeDefinitionGenericEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, PLUS_ONE_FORMULA, relay);
    assertNotEquals(awardUpvoteEvent, awardUpvoteEventDifferentIdentity);
    assertNotEquals(awardUpvoteEventDifferentIdentity, awardUpvoteEvent);

    FormulaEvent formulaEventUpvoteDtoDifferentIdentity = new FormulaEvent(Identity.generateRandomIdentity(), upvoteIdentifierTag, awardUpvoteEvent, PLUS_ONE_FORMULA, relay);
    assertNotEquals(curatedFormulaEventUpvote, formulaEventUpvoteDtoDifferentIdentity);
    assertNotEquals(formulaEventUpvoteDtoDifferentIdentity, curatedFormulaEventUpvote);
  }

  @Test
  void testGenericEventRecordFormulaEventCreation() {
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
  void testDifferentContentDto() {
    BadgeDefinitionGenericEvent differentContentDto = new BadgeDefinitionGenericEvent(
       identity, upvoteIdentifierTag, BaseIntegrationTestFixtures.AWARD_UNIT_UPVOTE, relay);

    assertNotEquals(curatedFormulaEventUpvote, new FormulaEvent(identity, upvoteIdentifierTag, differentContentDto, "+2", relay));
  }

  @Test
  void formulaContentTest() {
    assertEquals(
       "BadgeDefinitionReputationEvent CuratedFormulaEvent(s) operator(s) default content: BADGE_DEFN_UNIT_REP == (previous)BADGE_DEFN_UNIT_REP +1(BDG_DEF_UNIT_UP) -1(BDG_DEF_UNIT_DOWN)",
       new BadgeDefinitionReputationEvent(
          identity,
          reputationDefinitionCreatorPublicKey,
          new IdentifierTag(
             BaseIntegrationTestFixtures.TEST_UNIT_REPUTATION),
          BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
          relay,
          List.of(
             curatedFormulaEventUpvote,
             curatedFormulaEventDownvote)).getContent());

    String UNIT_UPVOTE_UNIQUE = "UNIT_UPVOTE_UNIQUE";
    String UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA = "+1";
    IdentifierTag upvoteUniqueIdentifierTag = new IdentifierTag(UNIT_UPVOTE_UNIQUE);
    BadgeDefinitionGenericEvent awardUniqueUpvoteEvent = new BadgeDefinitionGenericEvent(identity, upvoteUniqueIdentifierTag, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA, relay);

    CuratedFormulaEvent uniqueFormulaEvent = new CuratedFormulaEvent(aImgIdentity,
       new FormulaEvent(identity, upvoteIdentifierTag, awardUniqueUpvoteEvent, UNIT_UPVOTE_UNIQUE_PLUS_ONE_FORMULA, relay),
       new ReferenceTag(relay.getUrl()),
       relay);

    assertEquals(
       "BadgeDefinitionReputationEvent CuratedFormulaEvent(s) operator(s) default content: BADGE_DEFN_UNIT_REP == (previous)BADGE_DEFN_UNIT_REP +1(BDG_DEF_UNIT_UP) +1(UNIT_UPVOTE_UNIQUE)",
       new BadgeDefinitionReputationEvent(
          identity,
          reputationDefinitionCreatorPublicKey,
          new IdentifierTag(
             BaseIntegrationTestFixtures.TEST_UNIT_REPUTATION),
          BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
          relay,
          List.of(
             curatedFormulaEventUpvote,
             uniqueFormulaEvent)).getContent());
  }
}
