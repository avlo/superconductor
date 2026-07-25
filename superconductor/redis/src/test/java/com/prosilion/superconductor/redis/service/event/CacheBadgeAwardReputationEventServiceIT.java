package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.math.BigDecimal;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheBadgeAwardReputationEventServiceIT extends BaseIntegrationTestFixtures {
  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;
  private final CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService;

  private final EventServiceIF eventServiceIF;
  private final Relay relay;

  @Autowired
  public CacheBadgeAwardReputationEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUri,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheBadgeAwardReputationEventService") CacheBadgeAwardReputationEventService cacheBadgeAwardReputationEventService) throws ParseException {
    super(superconductorInstanceIdentity);
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeAwardReputationEventService = cacheBadgeAwardReputationEventService;
    this.relay = new Relay(relayUri);

    BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent = new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
    cacheServiceIF.save(badgeDefinitionUpvoteEvent);

    eventServiceIF.processIncomingEvent(
       new EventMessage(
          new FormulaEvent(
             formulaCreator,
             upvoteIdentifierTag,
             relay,
             badgeDefinitionUpvoteEvent,
             PLUS_ONE_FORMULA)), relay);

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       relay,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       new FormulaEvent(formulaCreator, upvoteIdentifierTag, relay, badgeDefinitionUpvoteEvent, PLUS_ONE_FORMULA));

    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneFormula), relay);

    eventServiceIF.processIncomingEvent(
       new EventMessage(
          new BadgeAwardGenericEvent<>(
             aImgIdentity,
             recipient.getPublicKey(),
             badgeDefinitionUpvoteEvent,
             relay)), relay);
  }

  @Test
  public void testSaveBadgeAwardReputationEventUpvote() {
    BadgeAwardReputationEvent badgeAwardReputationEvent = new BadgeAwardReputationEvent(
       aImgIdentity,
       recipient.getPublicKey(),
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationEventPlusOneFormula,
       BigDecimal.ZERO,
       relay);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardReputationEvent), relay);
    BadgeAwardReputationEvent dbRepAwardEvent = cacheBadgeAwardReputationEventService.materialize(badgeAwardReputationEvent.asGenericEventRecord()).get();
    assertEquals(badgeDefinitionReputationEventPlusOneFormula, dbRepAwardEvent.getBadgeDefinitionEvent());
  }
}
