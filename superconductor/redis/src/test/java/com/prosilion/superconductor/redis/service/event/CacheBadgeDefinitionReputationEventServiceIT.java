package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeDefinitionReputationEventService.NON_EXISTENT_ADDRESS_TAG_S;
import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_REPUTATION;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheBadgeDefinitionReputationEventServiceIT {
  public static final Relay relay = new Relay("ws://localhost:5555");

  public static final String PLUS_ONE_FORMULA = "+1";

  public final IdentifierTag reputationIdentifierTag = new IdentifierTag(TEST_UNIT_REPUTATION);
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  public static final String PLATFORM = CacheBadgeDefinitionReputationEventServiceIT.class.getPackageName();
  public static final String IDENTITY = CacheBadgeDefinitionReputationEventServiceIT.class.getSimpleName();
  public static final String PROOF = String.valueOf(CacheBadgeDefinitionReputationEventServiceIT.class.hashCode());

  private final BadgeDefinitionAwardEvent awardUpvoteDefinitionEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay);

  private final FormulaEvent plusOneFormulaEvent = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
  private final EventPluginIF eventPlugin;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  public CacheBadgeDefinitionReputationEventServiceIT(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheBadgeDefinitionReputationEventService") CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) throws ParseException {
    this.eventPlugin = eventPlugin;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;

    eventPlugin.processIncomingEvent(awardUpvoteDefinitionEvent);
    eventPlugin.processIncomingEvent(plusOneFormulaEvent);
  }

  @Test
  public void testSaveBadgeDefinitionReputationEventUpvote() throws ParseException {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
        identity,
        reputationIdentifierTag,
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        plusOneFormulaEvent);

    eventPlugin.processIncomingEvent(badgeDefinitionReputationEventPlusOneFormula);
    BadgeDefinitionReputationEvent dbRepDefnEvent = cacheBadgeDefinitionReputationEventService.getEvent(badgeDefinitionReputationEventPlusOneFormula.getId()).orElseThrow();
    assertTrue(dbRepDefnEvent.getFormulaEvents().contains(plusOneFormulaEvent));
    assertEquals(reputationIdentifierTag, dbRepDefnEvent.getIdentifierTag());
    assertEquals(BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG, dbRepDefnEvent.getExternalIdentityTag());
    assertEquals(badgeDefinitionReputationEventPlusOneFormula, dbRepDefnEvent);

    assertTrue(dbRepDefnEvent.getFormulaEvents().stream()
        .map(FormulaEvent::getFormula)
        .toList().contains(PLUS_ONE_FORMULA));

    assertTrue(dbRepDefnEvent.getFormulaEvents().stream()
        .map(FormulaEvent::getBadgeDefinitionAwardEvent)
        .map(BadgeDefinitionAwardEvent::getIdentifierTag)
        .map(IdentifierTag::getUuid).toList().contains(TEST_UNIT_UPVOTE));

    String BADGE_DEFINITION_VOTE = "BADGE_DEFINITION_VOTE";
    String MINUS_ONE_FORMULA = "-1";
    IdentifierTag downvoteIdentifierTag = new IdentifierTag(BADGE_DEFINITION_VOTE);

    BadgeDefinitionAwardEvent awardDownvoteDefinitionEvent = new BadgeDefinitionAwardEvent(identity, downvoteIdentifierTag, relay);
    FormulaEvent minusOneFormulaEvent = new FormulaEvent(identity, downvoteIdentifierTag, relay, awardDownvoteDefinitionEvent, MINUS_ONE_FORMULA);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneMinusOne = new BadgeDefinitionReputationEvent(
        identity,
        reputationIdentifierTag,
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        List.of(plusOneFormulaEvent, minusOneFormulaEvent));

    IdentifierTag uniqueIdentifierTag = new IdentifierTag("UNIQUE_" + TEST_UNIT_REPUTATION);
    BadgeDefinitionReputationEvent uniqueBadgeDefinitionReputationEventIdentifierTag = new BadgeDefinitionReputationEvent(
        identity,
        uniqueIdentifierTag,
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        List.of(plusOneFormulaEvent, minusOneFormulaEvent));

    String messageMissingEventId = assertThrows(NostrException.class, () -> eventPlugin.processIncomingEvent(uniqueBadgeDefinitionReputationEventIdentifierTag)).getMessage();
    assertTrue(
        messageMissingEventId.contains(
            String.format(
                String.join("", NON_EXISTENT_ADDRESS_TAG_S),
                uniqueBadgeDefinitionReputationEventIdentifierTag.serialize(),
                uniqueBadgeDefinitionReputationEventIdentifierTag.getId())));

    eventPlugin.processIncomingEvent(awardDownvoteDefinitionEvent);
    eventPlugin.processIncomingEvent(minusOneFormulaEvent);
    eventPlugin.processIncomingEvent(uniqueBadgeDefinitionReputationEventIdentifierTag);

    BadgeDefinitionReputationEvent dbRepDefnEventPlusMinus = cacheBadgeDefinitionReputationEventService.getEvent(uniqueBadgeDefinitionReputationEventIdentifierTag.getId()).orElseThrow();
    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().contains(plusOneFormulaEvent));
    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().contains(minusOneFormulaEvent));
    assertEquals(uniqueIdentifierTag, dbRepDefnEventPlusMinus.getIdentifierTag());
    assertEquals(BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG, dbRepDefnEventPlusMinus.getExternalIdentityTag());
    assertEquals(uniqueBadgeDefinitionReputationEventIdentifierTag, dbRepDefnEventPlusMinus);
    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().stream()
        .map(FormulaEvent::getFormula)
        .toList().contains(MINUS_ONE_FORMULA));
    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().stream()
        .map(FormulaEvent::getBadgeDefinitionAwardEvent)
        .map(BadgeDefinitionAwardEvent::getIdentifierTag)
        .map(IdentifierTag::getUuid).toList().contains(BADGE_DEFINITION_VOTE));
  }
}
