package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_DOWNVOTE;
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
  public static final String PLUS_ONE_FORMULA = "+1";
  private static final String FORMULA_UNIT_UPVOTE = "FORMULA_UNIT_UPVOTE";
  private static final String FORMULA_UNIT_DOWNVOTE = "FORMULA_UNIT_DOWNVOTE";

  public final IdentifierTag reputationIdentifierTag = new IdentifierTag(TEST_UNIT_REPUTATION);
  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);
  public final IdentifierTag downvoteIdentifierTag = new IdentifierTag(TEST_UNIT_DOWNVOTE);

  public final Identity identity = Identity.generateRandomIdentity();
  public final PublicKey reputationDefinitionCreatorPublicKey = Identity.generateRandomIdentity().getPublicKey();

  private final FormulaEvent plusOneFormulaEvent;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  BadgeDefinitionGenericEvent awardDownvoteDefinitionEvent;

  @Autowired
  public CacheBadgeDefinitionReputationEventServiceIT(
      @Value("${superconductor.relay.url}") String relayUri,
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
      @NonNull @Qualifier("cacheBadgeDefinitionReputationEventService") CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) throws ParseException {
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
    this.relay = new Relay(relayUri);

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay);
    cacheServiceIF.save(awardUpvoteDefinitionEvent);

    this.awardDownvoteDefinitionEvent = new BadgeDefinitionGenericEvent(identity, downvoteIdentifierTag, relay);
    cacheServiceIF.save(this.awardDownvoteDefinitionEvent);

    IdentifierTag formulaUnitUpvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_UPVOTE);
    plusOneFormulaEvent = new FormulaEvent(
        identity,
        formulaUnitUpvoteIdentifierTag,
        relay,
        awardUpvoteDefinitionEvent,
        PLUS_ONE_FORMULA);
    
    eventServiceIF.processIncomingEvent(new EventMessage(plusOneFormulaEvent));
  }

  @Test
  public void testSaveBadgeDefinitionReputationEventUpvote() throws ParseException {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
        identity,
        reputationDefinitionCreatorPublicKey,
        reputationIdentifierTag,
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        plusOneFormulaEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneFormula));
    BadgeDefinitionReputationEvent dbRepDefnEvent = cacheBadgeDefinitionReputationEventService.getEvent(badgeDefinitionReputationEventPlusOneFormula.getId(), relay.getUrl()).orElseThrow();
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
        .map(IdentifierTag::getUuid).toList().contains(TEST_UNIT_UPVOTE));

    String MINUS_ONE_FORMULA = "-1";
    IdentifierTag formulaUnitDownvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_DOWNVOTE);
    FormulaEvent minusOneFormulaEvent = new FormulaEvent(identity, formulaUnitDownvoteIdentifierTag, relay, awardDownvoteDefinitionEvent, MINUS_ONE_FORMULA);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneMinusOne = new BadgeDefinitionReputationEvent(
        identity,
        reputationDefinitionCreatorPublicKey,
        reputationIdentifierTag,
        relay,
        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
        List.of(plusOneFormulaEvent, minusOneFormulaEvent));

//    IdentifierTag uniqueIdentifierTag = new IdentifierTag("UNIQUE_" + TEST_UNIT_REPUTATION);
//    BadgeDefinitionReputationEvent uniqueBadgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
//        identity,
//        uniqueIdentifierTag,
//        relay,
//        BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
//        List.of(plusOneFormulaEvent, minusOneFormulaEvent));

    assertThrows(NostrException.class, () -> cacheBadgeDefinitionReputationEventService.materialize(badgeDefinitionReputationEventPlusOneMinusOne.asGenericEventRecord()));
//    assertTrue(
//        messageMissingEventId.contains(
//            String.format(
//                String.join("", NON_EXISTENT_ADDRESS_TAG_S),
//                uniqueBadgeDefinitionReputationEventIdentifierTag.serialize(),
//                uniqueBadgeDefinitionReputationEventIdentifierTag.getId())));

    eventServiceIF.processIncomingEvent(new EventMessage(minusOneFormulaEvent));
    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneMinusOne));

    BadgeDefinitionReputationEvent dbRepDefnEventPlusMinus = cacheBadgeDefinitionReputationEventService.getEvent(badgeDefinitionReputationEventPlusOneMinusOne.getId(), relay.getUrl()).orElseThrow();
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

    BadgeDefinitionReputationEvent reconstructed = cacheBadgeDefinitionReputationEventService.materialize(badgeDefinitionReputationEventPlusOneMinusOne.asGenericEventRecord());
    assertEquals(dbRepDefnEventPlusMinus, reconstructed);
//    assertTrue(
//        messageMissingEventId.contains(
//            String.format(
//                String.join("", NON_EXISTENT_ADDRESS_TAG_S),
//                uniqueBadgeDefinitionReputationEventIdentifierTag.serialize(),
//                uniqueBadgeDefinitionReputationEventIdentifierTag.getId())));

    AddressTag formulaEventAddressableEventAddressTag = plusOneFormulaEvent.asAddressableEventAddressTag();
    List<BadgeDefinitionReputationEvent> byDirectTag = cacheBadgeDefinitionReputationEventService.getByDirectTag(formulaEventAddressableEventAddressTag);
    System.out.println("111111111111111111111");
    System.out.println("111111111111111111111");
    System.out.println(byDirectTag.stream().map(EventIF::createPrettyPrintJson));
    System.out.println("111111111111111111111");
    System.out.println("111111111111111111111");
  }
}
