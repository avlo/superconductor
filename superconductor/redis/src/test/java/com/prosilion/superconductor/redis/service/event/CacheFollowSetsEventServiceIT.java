package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.enums.AfterimageKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_REPUTATION;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheFollowSetsEventServiceIT {
  public static final String PLUS_ONE_FORMULA = "+1";

  public final IdentifierTag reputationIdentifierTag = new IdentifierTag(TEST_UNIT_REPUTATION);
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);

  public final Identity identity = Identity.generateRandomIdentity();
  private final PublicKey reputationRecipientPublicKey = Identity.generateRandomIdentity().getPublicKey();

  private final BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  public CacheFollowSetsEventServiceIT(
      @Value("${superconductor.relay.url}") String relayUri,
      @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
      @NonNull @Qualifier("cacheFollowSetsEventService") CacheFollowSetsEventServiceIF cacheFollowSetsEventService) throws ParseException {
    this.eventServiceIF = eventServiceIF;
    this.cacheFollowSetsEventService = cacheFollowSetsEventService;
    this.relay = new Relay(relayUri);

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay);
    eventServiceIF.processIncomingEvent(new EventMessage(awardUpvoteDefinitionEvent));

    FormulaEvent plusOneFormulaEvent = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
    eventServiceIF.processIncomingEvent(new EventMessage(plusOneFormulaEvent));

    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
        identity,
        reputationIdentifierTag,
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        plusOneFormulaEvent);
    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneFormula));

    this.badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
        identity,
        reputationRecipientPublicKey,
        relay,
        badgeDefinitionReputationEventPlusOneFormula);
    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardUpvoteEvent));
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

    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent));

    FollowSetsEvent dbFollowSetsEventByEventId = cacheFollowSetsEventService.getEvent(followSetsEvent.getId(), relay.getUrl()).orElseThrow();
    assertEquals(followSetsEvent, dbFollowSetsEventByEventId);

    List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardAbstractEvents = dbFollowSetsEventByEventId.getBadgeAwardGenericEvents();
    assertTrue(badgeAwardAbstractEvents.contains(badgeAwardUpvoteEvent));

    PublicKey matchPubkey = Filterable.getTypeSpecificTags(PubKeyTag.class, dbFollowSetsEventByEventId).stream().map(PubKeyTag::getPublicKey).findFirst().orElseThrow();
    assertEquals(matchPubkey, reputationRecipientPublicKey);

    assertEquals(followSetsEvent.getContainedAddressableEvents(), dbFollowSetsEventByEventId.getContainedAddressableEvents());
  }
}
