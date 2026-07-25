package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheBadgeDefinitionReputationEventServiceIT extends BaseIntegrationTestFixtures {
  private final FormulaEvent plusOneFormulaEvent;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  BadgeDefinitionGenericEvent awardDownvoteDefinitionEvent;

  @Autowired
  public CacheBadgeDefinitionReputationEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUri,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheBadgeDefinitionReputationEventService") CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) throws ParseException {
    super(superconductorInstanceIdentity);
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
    this.relay = new Relay(relayUri);

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
    cacheServiceIF.save(awardUpvoteDefinitionEvent);

    this.awardDownvoteDefinitionEvent = new BadgeDefinitionGenericEvent(upvoteDefnCreator, downvoteIdentifierTag, relay);
    cacheServiceIF.save(this.awardDownvoteDefinitionEvent);

    plusOneFormulaEvent = new FormulaEvent(
       formulaCreator,
       formulaUpvoteIdentifierTag,
       relay,
       awardUpvoteDefinitionEvent,
       PLUS_ONE_FORMULA);

    eventServiceIF.processIncomingEvent(new EventMessage(plusOneFormulaEvent), relay);
  }

  @Test
  public void testSaveBadgeDefinitionReputationEventUpvote() throws ParseException {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       relay,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       plusOneFormulaEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneFormula), relay);
    BadgeDefinitionReputationEvent dbRepDefnEvent = cacheBadgeDefinitionReputationEventService.getEvent(badgeDefinitionReputationEventPlusOneFormula.getId(), relay).orElseThrow();
    assertTrue(dbRepDefnEvent.getFormulaEvents().contains(plusOneFormulaEvent));
    assertEquals(reputationIdentifierTag, dbRepDefnEvent.getIdentifierTag());
    assertEquals(BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG, dbRepDefnEvent.getExternalIdentityTag());
    assertEquals(badgeDefinitionReputationEventPlusOneFormula, dbRepDefnEvent);

    assertTrue(dbRepDefnEvent.getFormulaEvents().stream()
       .map(FormulaEvent::getFormula)
       .toList().contains(PLUS_ONE_FORMULA));

    assertTrue(dbRepDefnEvent.getFormulaEvents().stream()
       .map(FormulaEvent::getBadgeDefinitionGenericEvent)
       .map(BadgeDefinitionGenericEvent::getIdentifierTag)
       .map(IdentifierTag::getUuid).toList().contains(AWARD_UNIT_UPVOTE));

    String MINUS_ONE_FORMULA = "-1";
    IdentifierTag formulaUnitDownvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_DOWNVOTE);
    FormulaEvent minusOneFormulaEvent = new FormulaEvent(formulaCreator, formulaUnitDownvoteIdentifierTag, relay, awardDownvoteDefinitionEvent, MINUS_ONE_FORMULA);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneMinusOne = new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       relay,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       List.of(plusOneFormulaEvent, minusOneFormulaEvent));

    assertThrows(NostrException.class, () -> cacheBadgeDefinitionReputationEventService.materialize(badgeDefinitionReputationEventPlusOneMinusOne.asGenericEventRecord()));

    eventServiceIF.processIncomingEvent(new EventMessage(minusOneFormulaEvent), relay);
    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneMinusOne), relay);

    BadgeDefinitionReputationEvent dbRepDefnEventPlusMinus = cacheBadgeDefinitionReputationEventService.getEvent(badgeDefinitionReputationEventPlusOneMinusOne.getId(), relay).orElseThrow();
    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().contains(plusOneFormulaEvent));
    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().contains(minusOneFormulaEvent));
    assertEquals(reputationIdentifierTag, dbRepDefnEventPlusMinus.getIdentifierTag());
    assertEquals(BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG, dbRepDefnEventPlusMinus.getExternalIdentityTag());
    assertEquals(badgeDefinitionReputationEventPlusOneMinusOne, dbRepDefnEventPlusMinus);
    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().stream()
       .map(FormulaEvent::getFormula)
       .toList().contains(MINUS_ONE_FORMULA));
    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().stream()
       .map(FormulaEvent::getBadgeDefinitionGenericEvent)
       .map(BadgeDefinitionGenericEvent::getIdentifierTag).toList().contains(downvoteIdentifierTag));

    BadgeDefinitionReputationEvent reconstructed = cacheBadgeDefinitionReputationEventService.materialize(badgeDefinitionReputationEventPlusOneMinusOne.asGenericEventRecord()).orElseThrow();
    assertEquals(dbRepDefnEventPlusMinus, reconstructed);
  }
}
