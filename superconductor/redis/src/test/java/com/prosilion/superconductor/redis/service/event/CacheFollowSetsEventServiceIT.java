package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardGenericVoteEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheFollowSetsEventServiceIT {
  public static final Relay relay = new Relay("ws://localhost:5555");

  public static final String REPUTATION = "TEST_REPUTATION";
  public static final String UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
  public static final String PLUS_ONE_FORMULA = "+1";

  public final IdentifierTag reputationIdentifierTag = new IdentifierTag(REPUTATION);
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(UNIT_UPVOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  public static final String PLATFORM = CacheFollowSetsEventServiceIT.class.getPackageName();
  public static final String IDENTITY = CacheFollowSetsEventServiceIT.class.getSimpleName();
  public static final String PROOF = String.valueOf(CacheFollowSetsEventServiceIT.class.hashCode());

  private final BadgeDefinitionAwardEvent awardUpvoteDefinitionEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay);
  private final FormulaEvent plusOneFormulaEvent = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
      identity,
      reputationIdentifierTag,
      relay,
      new ExternalIdentityTag(PLATFORM, IDENTITY, PROOF),
      plusOneFormulaEvent);

  private final PublicKey reputationRecipientPublicKey = Identity.generateRandomIdentity().getPublicKey();
  private final BadgeAwardGenericVoteEvent badgeAwardUpvoteEvent = new BadgeAwardGenericVoteEvent(
      identity,
      reputationRecipientPublicKey,
      badgeDefinitionReputationEventPlusOneFormula);

  private final EventPluginIF eventPlugin;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventService;

  public CacheFollowSetsEventServiceIT(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheFollowSetsEventService") CacheFollowSetsEventServiceIF cacheFollowSetsEventService) throws ParseException {
    this.eventPlugin = eventPlugin;
    this.cacheFollowSetsEventService = cacheFollowSetsEventService;

    eventPlugin.processIncomingEvent(awardUpvoteDefinitionEvent);
    eventPlugin.processIncomingEvent(plusOneFormulaEvent);
    eventPlugin.processIncomingEvent(badgeDefinitionReputationEventPlusOneFormula);
    eventPlugin.processIncomingEvent(badgeAwardUpvoteEvent);
  }

  @Test
  public void testSaveBadgeAwardReputationEventUpvote() {
    final String FOLLOW_SETS_EVENT = "FOLLOW_SETS_EVENT";
    final IdentifierTag followSetsIdentifierTag = new IdentifierTag(FOLLOW_SETS_EVENT);

    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
        identity,
        reputationRecipientPublicKey,
        followSetsIdentifierTag,
        relay,
        List.of(badgeAwardUpvoteEvent));

    eventPlugin.processIncomingEvent(followSetsEvent);

    FollowSetsEvent dbFollowSetsEvent = cacheFollowSetsEventService.getEvent(followSetsEvent.getId()).orElseThrow();
    List<BadgeAwardGenericVoteEvent> badgeAwardAbstractEvents = dbFollowSetsEvent.getBadgeAwardAbstractEvents();
    assertTrue(badgeAwardAbstractEvents.contains(badgeAwardUpvoteEvent));
  }
}
