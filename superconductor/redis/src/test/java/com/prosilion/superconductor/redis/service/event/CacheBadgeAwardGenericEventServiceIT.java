package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.BadgeAwardGenericVoteEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeAwardGenericVoteEventService;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheBadgeAwardGenericEventServiceIT {
  public static final Relay relay = new Relay("ws://localhost:5555");

  public static final String REPUTATION = "TEST_REPUTATION";
  public static final String UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
  public static final String PLUS_ONE_FORMULA = "+1";

  public final IdentifierTag reputationIdentifierTag = new IdentifierTag(REPUTATION);
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(UNIT_UPVOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  public static final String PLATFORM = CacheBadgeAwardGenericEventServiceIT.class.getPackageName();
  public static final String IDENTITY = CacheBadgeAwardGenericEventServiceIT.class.getSimpleName();
  public static final String PROOF = String.valueOf(CacheBadgeAwardGenericEventServiceIT.class.hashCode());

  private final BadgeDefinitionAwardEvent awardUpvoteDefinitionEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay);

  private final FormulaEvent plusOneFormulaEvent = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
  private final ExternalIdentityTag externalIdentityTag = new ExternalIdentityTag(PLATFORM, IDENTITY, PROOF);

  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;

  private final EventPluginIF eventPlugin;
  private final CacheBadgeAwardGenericVoteEventService cacheBadgeAwardGenericEventService;

  public CacheBadgeAwardGenericEventServiceIT(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheBadgeAwardGenericEventService") CacheBadgeAwardGenericVoteEventService cacheBadgeAwardGenericEventService) throws ParseException {
    this.eventPlugin = eventPlugin;
    this.cacheBadgeAwardGenericEventService = cacheBadgeAwardGenericEventService;

    eventPlugin.processIncomingEvent(awardUpvoteDefinitionEvent);
    eventPlugin.processIncomingEvent(plusOneFormulaEvent);

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
        identity,
        reputationIdentifierTag,
        relay,
        externalIdentityTag,
        plusOneFormulaEvent);

    eventPlugin.processIncomingEvent(badgeDefinitionReputationEventPlusOneFormula);
  }

  @Test
  public void testSaveBadgeAwardReputationEventUpvote() {
    PublicKey upvotedUserPublicKey = Identity.generateRandomIdentity().getPublicKey();
    BadgeAwardGenericVoteEvent badgeAwardGenericVoteEvent = new BadgeAwardGenericVoteEvent(
        identity,
        upvotedUserPublicKey,
        badgeDefinitionReputationEventPlusOneFormula);

    eventPlugin.processIncomingEvent(badgeAwardGenericVoteEvent);
    BadgeAwardAbstractEvent dbGenericAwardEvent = cacheBadgeAwardGenericEventService.getEvent(badgeAwardGenericVoteEvent.getId()).orElseThrow();
    assertEquals(badgeDefinitionReputationEventPlusOneFormula, dbGenericAwardEvent.getAddressableEvent());
  }
}
