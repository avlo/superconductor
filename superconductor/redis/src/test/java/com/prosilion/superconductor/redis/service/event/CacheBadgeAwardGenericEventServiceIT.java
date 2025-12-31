package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_REPUTATION;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheBadgeAwardGenericEventServiceIT {
  public static final Relay relay = new Relay("ws://localhost:5555");

  public static final String PLUS_ONE_FORMULA = "+1";

  public final IdentifierTag reputationIdentifierTag = new IdentifierTag(TEST_UNIT_REPUTATION);
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  private final BadgeDefinitionAwardEvent awardUpvoteDefinitionEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay);

  private final FormulaEvent plusOneFormulaEvent = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;

  private final EventPluginIF eventPlugin;
  private final CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF;

  public CacheBadgeAwardGenericEventServiceIT(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheBadgeAwardGenericEventServiceIF cacheBadgeAwardGenericEventServiceIF) throws ParseException {
    this.eventPlugin = eventPlugin;
    this.cacheBadgeAwardGenericEventServiceIF = cacheBadgeAwardGenericEventServiceIF;

    eventPlugin.processIncomingEvent(awardUpvoteDefinitionEvent);
    eventPlugin.processIncomingEvent(plusOneFormulaEvent);

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
        identity,
        reputationIdentifierTag,
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        plusOneFormulaEvent);

    eventPlugin.processIncomingEvent(badgeDefinitionReputationEventPlusOneFormula);
  }

  @Test
  public void testSaveBadgeAwardReputationEventUpvote() {
    PublicKey upvotedUserPublicKey = Identity.generateRandomIdentity().getPublicKey();
    BadgeAwardGenericEvent badgeAwardGenericVoteEvent = new BadgeAwardGenericEvent(
        identity,
        upvotedUserPublicKey,
        badgeDefinitionReputationEventPlusOneFormula);

    eventPlugin.processIncomingEvent(badgeAwardGenericVoteEvent);
    BadgeAwardGenericEvent dbGenericAwardEvent = cacheBadgeAwardGenericEventServiceIF.getEvent(badgeAwardGenericVoteEvent.getId()).orElseThrow();
    assertEquals(badgeAwardGenericVoteEvent, dbGenericAwardEvent);
  }
}
