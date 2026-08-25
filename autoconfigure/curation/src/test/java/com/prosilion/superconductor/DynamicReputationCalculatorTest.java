package com.prosilion.superconductor;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.calculator.ReputationCalculator;
import com.prosilion.superconductor.base.BaseTestFixtures;
import java.math.BigDecimal;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ActiveProfiles("test")
public class DynamicReputationCalculatorTest extends BaseTestFixtures {
  private final BadgeDefinitionReputationEvent badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent;

  private final CuratedFormulaEvent plusOneCuratedFormulaEvent;
  private final CuratedFormulaEvent minusOneCuratedFormulaEvent;
  private final BadgeAwardReputationEvent emptyNoReputationYetBadgeAwardEvent;
  private final ReputationCalculator reputationCalculator;

  BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;
  BadgeDefinitionGenericEvent awardDownvoteDefinitionEvent;

  public DynamicReputationCalculatorTest() {
    super(Identity.create("fa11661b5f43c8f18f11861b4d553c47337dac9e351083b27320e311b7b324ac"));
    this.reputationCalculator = new ReputationCalculator(this.superconductorInstanceIdentity, relay.getUrl());

    this.awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(upvoteDefnCreator, formulaUpvoteIdentifierTag, relay);
    this.awardDownvoteDefinitionEvent = new BadgeDefinitionGenericEvent(upvoteDefnCreator, formulaDownvoteIdentifierTag, relay);

    this.plusOneCuratedFormulaEvent =
       new CuratedFormulaEvent(
          superconductorInstanceIdentity,
          new FormulaEvent(
             formulaCreator,
             formulaUpvoteIdentifierTag,
             awardUpvoteDefinitionEvent,
             PLUS_ONE_FORMULA,
             relay),
          new ReferenceTag(relay.getUrl()),
          relay);

    this.minusOneCuratedFormulaEvent =
       new CuratedFormulaEvent(
          superconductorInstanceIdentity,
          new FormulaEvent(
             formulaCreator,
             formulaDownvoteIdentifierTag,
             awardDownvoteDefinitionEvent,
             MINUS_ONE_FORMULA,
             relay),
          new ReferenceTag(relay.getUrl()),
          relay);

    this.badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent = new BadgeDefinitionReputationEvent(
       repDefnCreator,
       superconductorInstanceIdentity.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent);

    this.emptyNoReputationYetBadgeAwardEvent = new BadgeAwardReputationEvent(
       superconductorInstanceIdentity,
       recipient.getPublicKey(),
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       new BigDecimal("0"),
       relay);
  }

  @Test
  void testCalculatorZeroPlusOne() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEvent = createBadgeAwardEvent(awardUpvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curationSetsEvent = new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardEvent,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().orElseThrow().getUrl()),
       new ReferenceTag(badgeAwardEvent.getRelay().orElseThrow().getUrl()),
       relay);

    BadgeSetsEvent badgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       curationSetsEvent,
       relay);

    BadgeAwardReputationEvent badgeAwardReputationEventWithSingleFormulaEvent = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       emptyNoReputationYetBadgeAwardEvent,
       List.of(plusOneCuratedFormulaEvent),
       badgeSetsEvent.getCuratedBadgeAwardGenericEventList());
    assertEquals("1", badgeAwardReputationEventWithSingleFormulaEvent.getContent());

    BadgeAwardReputationEvent badgeAwardReputationEventWithMultipleFormulaEvents = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       emptyNoReputationYetBadgeAwardEvent,
       List.of(plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent),
       badgeSetsEvent.getCuratedBadgeAwardGenericEventList());
    assertEquals("1", badgeAwardReputationEventWithMultipleFormulaEvents.getContent());

    assertThrows(NostrException.class, () -> reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       emptyNoReputationYetBadgeAwardEvent,
       List.of(),
       badgeSetsEvent.getCuratedBadgeAwardGenericEventList()));
  }

  @Test
  void testCalculatorOnePlusOne() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEvent = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       String.format("awardDefinitionEvent, definition creator PublicKey: [%s]", upvoteDefnCreator.getPublicKey()),
       relay);

    CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent = new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardEvent,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().orElseThrow().getUrl()),
       new ReferenceTag(badgeAwardEvent.getRelay().orElseThrow().getUrl()),
       relay);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
       repDefnCreator,
       superconductorInstanceIdentity.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent);

    BadgeSetsEvent badgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationEvent,
       curatedBadgeAwardGenericEvent,
       relay);

    BadgeAwardReputationEvent previousReputationEvent = new BadgeAwardReputationEvent(
       superconductorInstanceIdentity,
       recipient.getPublicKey(),
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationEvent,
       new BigDecimal("1"),
       relay);

    BadgeAwardReputationEvent badgeAwardReputationEvent = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       previousReputationEvent,
       List.of(plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent),
       badgeSetsEvent.getCuratedBadgeAwardGenericEventList());

    assertEquals("2", badgeAwardReputationEvent.getContent());
  }

  @Test
  void testCalculatorZeroMinusOne() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEvent = createBadgeAwardEvent(awardDownvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curationSetsEvent = getCuratedBadgeAwardEvent(badgeAwardEvent);

    BadgeSetsEvent badgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       curationSetsEvent,
       relay);

    BadgeAwardReputationEvent badgeAwardReputationEvent = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       emptyNoReputationYetBadgeAwardEvent,
       List.of(plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent),
       badgeSetsEvent.getCuratedBadgeAwardGenericEventList());

    assertEquals("-1", badgeAwardReputationEvent.getContent());
  }

  @Test
  void testCalculatorStartsWithMinusOneThenPlusOne() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = createBadgeAwardEvent(awardUpvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardUpvoteEvent = getCuratedBadgeAwardEvent(badgeAwardUpvoteEvent);

    BadgeAwardReputationEvent reputationFirstEventIsMinusOneBadgeAwardEvent = new BadgeAwardReputationEvent(
       superconductorInstanceIdentity,
       recipient.getPublicKey(),
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       new BigDecimal("-1"),
       relay);

    BadgeAwardReputationEvent badgeAwardReputationEvent = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       reputationFirstEventIsMinusOneBadgeAwardEvent,
       List.of(plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent),
       List.of(curatedBadgeAwardUpvoteEvent));

    assertEquals("0", badgeAwardReputationEvent.getContent());
  }

  @Test
  void testCalculatorZeroMinusOnePlusOne() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = createBadgeAwardEvent(awardUpvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardUpvoteEvent = getCuratedBadgeAwardEvent(badgeAwardUpvoteEvent);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardDownvoteEvent = createBadgeAwardEvent(awardDownvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardDownvoteEvent = getCuratedBadgeAwardEvent(badgeAwardDownvoteEvent);

    BadgeAwardReputationEvent reputationFirstEventIsMinusOneBadgeAwardEvent = new BadgeAwardReputationEvent(
       superconductorInstanceIdentity,
       recipient.getPublicKey(),
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       new BigDecimal("0"),
       relay);

    BadgeAwardReputationEvent badgeAwardReputationEvent = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       reputationFirstEventIsMinusOneBadgeAwardEvent,
       List.of(plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent),
       List.of(
          curatedBadgeAwardDownvoteEvent,
          curatedBadgeAwardUpvoteEvent));

    assertEquals("0", badgeAwardReputationEvent.getContent());
  }

  @Test
  void testCalculatorZeroPlusOneMinusOne() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = createBadgeAwardEvent(awardUpvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardUpvoteEvent = getCuratedBadgeAwardEvent(badgeAwardUpvoteEvent);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardDownvoteEvent = createBadgeAwardEvent(awardDownvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardDownvoteEvent = getCuratedBadgeAwardEvent(badgeAwardDownvoteEvent);

    BadgeSetsEvent badgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       List.of(
          curatedBadgeAwardUpvoteEvent,
          curatedBadgeAwardDownvoteEvent),
       relay);

    BadgeAwardReputationEvent badgeAwardReputationEvent = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       emptyNoReputationYetBadgeAwardEvent,
       List.of(plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent),
       badgeSetsEvent.getCuratedBadgeAwardGenericEventList());

    assertEquals("0", badgeAwardReputationEvent.getContent());
  }

  @Test
  void testCalculatorPreExistingReputationPlusOneMinusOne() {
    BadgeAwardReputationEvent existingBadgeAwardReputationEvent = new BadgeAwardReputationEvent(
       superconductorInstanceIdentity,
       recipient.getPublicKey(),
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       new BigDecimal("1"),
       relay);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent_1 = createBadgeAwardEvent(awardUpvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardUpvoteEvent_1 = getCuratedBadgeAwardEvent(badgeAwardUpvoteEvent_1);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardDownvoteEvent_1 = createBadgeAwardEvent(awardDownvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardDownvoteEvent_1 = getCuratedBadgeAwardEvent(badgeAwardDownvoteEvent_1);

    BadgeSetsEvent badgeSetsEvent_1 = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       List.of(
          curatedBadgeAwardUpvoteEvent_1,
          curatedBadgeAwardDownvoteEvent_1),
       relay);

    BadgeAwardReputationEvent firstRecalculatedBadgeAwardReputationEvent = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       existingBadgeAwardReputationEvent,
       List.of(plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent),
       badgeSetsEvent_1.getCuratedBadgeAwardGenericEventList());

    assertEquals("1", firstRecalculatedBadgeAwardReputationEvent.getContent());

    BadgeAwardReputationEvent secondRecalculatedBadgeAwardReputationEvent = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       firstRecalculatedBadgeAwardReputationEvent,
       List.of(plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent),
       badgeSetsEvent_1.getCuratedBadgeAwardGenericEventList());

//  below tests same badgeSetsEvent does not change reputation calculation/score
    assertEquals("1", secondRecalculatedBadgeAwardReputationEvent.getContent());

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent_2 = createBadgeAwardEvent(awardUpvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardUpvoteEvent_2 = getCuratedBadgeAwardEvent(badgeAwardUpvoteEvent_2);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardDownvoteEvent_2 = createBadgeAwardEvent(awardDownvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardDownvoteEvent_2 = getCuratedBadgeAwardEvent(badgeAwardDownvoteEvent_2);

    BadgeSetsEvent thirdBadgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       List.of(
          curatedBadgeAwardUpvoteEvent_1,
          curatedBadgeAwardDownvoteEvent_1,
          curatedBadgeAwardUpvoteEvent_2,
          curatedBadgeAwardDownvoteEvent_2),
       relay);

    BadgeAwardReputationEvent thirdRecalculatedBadgeAwardReputationEvent = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       secondRecalculatedBadgeAwardReputationEvent,
       List.of(plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent),
       thirdBadgeSetsEvent.getCuratedBadgeAwardGenericEventList());

    assertEquals("1", thirdRecalculatedBadgeAwardReputationEvent.getContent());
  }

  @Test
  void testCalculatorOneMinusOneVariant() {
    String VARIANT = "_VARIANT";
    IdentifierTag formulaUpvoteIdentifierTagVariant = new IdentifierTag(FORMULA_UNIT_UPVOTE + VARIANT);
    IdentifierTag badgeDefnIdentifierTagVariant = new IdentifierTag(AWARD_UNIT_UPVOTE + VARIANT);

    CuratedFormulaEvent secondFormulaShouldNotInterfereWithFirstFormula =
       new CuratedFormulaEvent(
          superconductorInstanceIdentity,
          new FormulaEvent(
             formulaCreator,
             formulaUpvoteIdentifierTagVariant,
             new BadgeDefinitionGenericEvent(upvoteDefnCreator, badgeDefnIdentifierTagVariant, relay),
             PLUS_ONE_FORMULA,
             relay),
          new ReferenceTag(relay.getUrl()),
          relay);

    BadgeDefinitionReputationEvent localBadgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent = new BadgeDefinitionReputationEvent(
       repDefnCreator,
       superconductorInstanceIdentity.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent, minusOneCuratedFormulaEvent);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = createBadgeAwardEvent(awardUpvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curationSetsEventUpvote = getCuratedBadgeAwardEvent(badgeAwardUpvoteEvent);

    BadgeSetsEvent badgeSetsEvent_1 = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       badgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       curationSetsEventUpvote, relay);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardDownvoteEvent = createBadgeAwardEvent(awardDownvoteDefinitionEvent);
    CuratedBadgeAwardGenericEvent curationSetsEventDownvote = getCuratedBadgeAwardEvent(badgeAwardDownvoteEvent);

    BadgeSetsEvent badgeSetsEvent_2 = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       localBadgeDefinitionReputationContainingPlusOneFormulaEventAndMinusOneFormulaEvent,
       curationSetsEventDownvote, relay);

    BadgeAwardReputationEvent badgeAwardReputationEvent_1 = reputationCalculator.calculateUpdatedReputationEvent(
       recipient.getPublicKey(),
       emptyNoReputationYetBadgeAwardEvent,
       List.of(
          plusOneCuratedFormulaEvent,
          secondFormulaShouldNotInterfereWithFirstFormula),
       badgeSetsEvent_1.getCuratedBadgeAwardGenericEventList());

    assertEquals("1", badgeAwardReputationEvent_1.getContent());
  }

  private @NonNull CuratedBadgeAwardGenericEvent getCuratedBadgeAwardEvent(BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEvent) {
    return new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardEvent,
       new ReferenceTag(badgeAwardEvent.getBadgeDefinitionEvent().getRelay().orElseThrow().getUrl()),
       new ReferenceTag(badgeAwardEvent.getRelay().orElseThrow().getUrl()),
       relay);
  }

  private BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createBadgeAwardEvent(BadgeDefinitionGenericEvent awardDefinitionEvent) {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardDefinitionEvent,
       String.format("awardDefinitionEvent, definition creator PublicKey: [%s]", upvoteDefnCreator.getPublicKey()),
       relay);
  }

//  @Test
//  void testCalculatorOnePlusTen() {
//    String AWARD_10_UPVOTE = "TEST_10_UPVOTE";
//    String FORMULA_10_UPVOTE = "FORMULA_TEN_UPVOTE";
//    IdentifierTag formulaIdentifierTag = new IdentifierTag(FORMULA_10_UPVOTE);
//    IdentifierTag reputationDefinitionIdentifierTag = new IdentifierTag(AWARD_10_UPVOTE);
//    FormulaEvent plusTenFormulaEvent = new FormulaEvent(
//      formulaCreator,
//      formulaIdentifierTag,
//      relay,
//      new BadgeDefinitionGenericEvent(upvoteDefnCreator, reputationDefinitionIdentifierTag, relay),
//      "+10");
//
//    BadgeDefinitionReputationEvent badgeDefinitionReputationEventAddTen = new BadgeDefinitionReputationEvent(
//      repDefnCreator,
//      submitter.getPublicKey(),
//      AbstractIT.reputationIdentifierTag,
//      relay,
//      BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
//      plusTenFormulaEvent);
//
//    BadgeAwardReputationEvent badgeAwardNoRepYet = new BadgeAwardReputationEvent(
//      afterimageInstanceIdentity,
//      recipient.getPublicKey(),
//      BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
//      badgeDefinitionReputationEventAddTen,
//      new BigDecimal("0"),
//      relay);
//
//    BadgeDefinitionGenericEventAux defnAuxNo_defnEvent_NoNo_Upvote = createDefnEventAux(badgeDefinitionReputationEventAddTen, null);
//    SetsPairedEvents badgeSetsUpvoteEventPairedEvents = new SetsPairedEvents(
//      defnAuxNo_defnEvent_NoNo_Upvote,
//      createAwardEventAux(createBadgeAwardEvent(reputationDefinitionIdentifierTag), relay));
//
//    BadgeSetsEvent badgeSetsDownvoteEvent = new BadgeSetsEvent(
//      submitter,
//      badgeDefinitionReputationEventAddTen,
//      badgeSetsUpvoteEventPairedEvents, relay);
//
//    FollowSetsEvent incomingFollowSetsEvent = new FollowSetsEvent(
//      afterimageInstanceIdentity,
//      badgeSetsDownvoteEvent,
//      relay);
//
//    BadgeAwardReputationEvent badgeAwardReputationEvent = dynamicReputationCalculator.calculateUpdatedReputationEvent(
//      recipient.getPublicKey(),
//      Optional.of(badgeAwardNoRepYet),
//      List.of(
//        plusTenFormulaEvent),
//      incomingFollowSetsEvent).get();
//
//    assertEquals("10", badgeAwardReputationEvent.getContent());

  //  }
}
