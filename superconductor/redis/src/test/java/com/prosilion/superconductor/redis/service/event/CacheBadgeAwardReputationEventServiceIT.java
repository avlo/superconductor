package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_REPUTATION;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheBadgeAwardReputationEventServiceIT {
  public static final Relay relay = new Relay("ws://localhost:5575");

  public static final String PLUS_ONE_FORMULA = "+1";

  public final IdentifierTag reputationIdentifierTag = new IdentifierTag(TEST_UNIT_REPUTATION);
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);

  public final Identity identity = Identity.generateRandomIdentity();
  public final PublicKey upvotedUserPublicKey = Identity.generateRandomIdentity().getPublicKey();

  private final BadgeDefinitionAwardEvent awardUpvoteDefinitionEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay);

  private final FormulaEvent plusOneFormulaEvent = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;

  private final EventServiceIF eventServiceIF;
  private final CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService;

  public CacheBadgeAwardReputationEventServiceIT(
      @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
      @NonNull @Qualifier("cacheBadgeAwardReputationEventService") CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService) throws ParseException {
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeAwardReputationEventService = cacheBadgeAwardReputationEventService;

    eventServiceIF.processIncomingEvent(new EventMessage(awardUpvoteDefinitionEvent));
    eventServiceIF.processIncomingEvent(new EventMessage(plusOneFormulaEvent));

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
        identity,
        reputationIdentifierTag,
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        plusOneFormulaEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneFormula));

    BadgeAwardGenericEvent badgeAwardGenericVoteEvent = new BadgeAwardGenericEvent(
        identity,
        upvotedUserPublicKey,
        awardUpvoteDefinitionEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardGenericVoteEvent));
  }

  @Test
  public void testSaveBadgeAwardReputationEventUpvote() {
    BadgeAwardReputationEvent badgeAwardReputationEvent = new BadgeAwardReputationEvent(
        identity,
        upvotedUserPublicKey,
        BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
        badgeDefinitionReputationEventPlusOneFormula,
        BigDecimal.ZERO);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardReputationEvent));
    BadgeAwardReputationEvent dbRepAwardEvent = cacheBadgeAwardReputationEventService.getEvent(badgeAwardReputationEvent.getId()).orElseThrow();
    assertEquals(badgeDefinitionReputationEventPlusOneFormula, dbRepAwardEvent.getBadgeDefinitionReputationEvent());
  }
}
