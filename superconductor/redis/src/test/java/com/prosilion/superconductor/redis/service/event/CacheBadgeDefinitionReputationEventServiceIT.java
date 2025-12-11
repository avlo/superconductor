//package com.prosilion.superconductor.redis.service.event;
//
//import com.ezylang.evalex.parser.ParseException;
//import com.prosilion.nostr.NostrException;
//import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
//import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
//import com.prosilion.nostr.event.FormulaEvent;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.event.internal.Relay;
//import com.prosilion.nostr.filter.Filterable;
//import com.prosilion.nostr.tag.ExternalIdentityTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeDefinitionReputationEventService;
//import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
//import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
//import com.prosilion.superconductor.lib.redis.service.RedisCacheService;
//import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.util.Strings;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.lang.NonNull;
//import org.springframework.test.context.ActiveProfiles;
//
//import static com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeDefinitionReputationEventService.DUPLICATE_REPUTATION_IDENTIFIER_TAG;
//import static com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService.NON_EXISTENT_EVENT_ID_S;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Slf4j
//@EmbeddedRedisStandalone
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
//public class CacheBadgeDefinitionReputationEventServiceIT {
//  public static final Relay relay = new Relay("ws://localhost:5555");
//
//  public static final String REPUTATION = "REPUTATION";
//  public static final String UNIT_UPVOTE = "UNIT_UPVOTE";
//  public static final String PLUS_ONE_FORMULA = "+1";
//
//  public final IdentifierTag reputationIdentifierTag = new IdentifierTag(REPUTATION);
//  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(UNIT_UPVOTE);
//
//  public final Identity identity = Identity.generateRandomIdentity();
//
//  public static final String PLATFORM = CacheBadgeDefinitionReputationEventServiceIT.class.getPackageName();
//  public static final String IDENTITY = CacheBadgeDefinitionReputationEventServiceIT.class.getSimpleName();
//  public static final String PROOF = String.valueOf(CacheBadgeDefinitionReputationEventServiceIT.class.hashCode());
//
//  private final BadgeDefinitionAwardEvent awardUpvoteDefinitionEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay);
//
//  private final FormulaEvent plusOneFormulaEvent = new FormulaEvent(identity, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
//  private final ExternalIdentityTag externalIdentityTag = new ExternalIdentityTag(PLATFORM, IDENTITY, PROOF);
//
//  private final EventPluginIF eventPlugin;
//  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;
//
//  public CacheBadgeDefinitionReputationEventServiceIT(
//      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
//      @NonNull @Qualifier("cacheBadgeDefinitionReputationEventService") CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService,
//      @NonNull @Qualifier("cacheFormulaEventService") CacheFormulaEventService cacheFormulaEventService,
//      @NonNull @Qualifier("cacheService") RedisCacheService cacheService) throws ParseException {
//    this.eventPlugin = eventPlugin;
//    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
//
//    eventPlugin.processIncomingEvent(awardUpvoteDefinitionEvent);
//    GenericEventRecord dbAwardUpvoteEvent = cacheService.getEventByEventId(awardUpvoteDefinitionEvent.getId()).orElseThrow();
//    assertEquals(awardUpvoteDefinitionEvent.getId(), dbAwardUpvoteEvent.getId());
//    assertEquals(UNIT_UPVOTE, Filterable.getTypeSpecificTags(IdentifierTag.class, dbAwardUpvoteEvent).getFirst().getUuid());
//
//    eventPlugin.processIncomingEvent(plusOneFormulaEvent);
//    FormulaEvent returnedUpvoteFormulaEvent = cacheFormulaEventService.getFormulaEvent(plusOneFormulaEvent.getId()).orElseThrow();
//    assertEquals(plusOneFormulaEvent, returnedUpvoteFormulaEvent);
//    assertEquals(PLUS_ONE_FORMULA, returnedUpvoteFormulaEvent.getContent());
//    assertEquals(UNIT_UPVOTE, returnedUpvoteFormulaEvent.getBadgeDefinitionAwardEvent().getIdentifierTag().getUuid());
//  }
//
//  @Test
//  public void testSaveBadgeDefinitionReputationEventUpvote() throws ParseException {
//    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
//        identity,
//        reputationIdentifierTag,
//        relay,
//        externalIdentityTag,
//        plusOneFormulaEvent);
//
//    eventPlugin.processIncomingEvent(badgeDefinitionReputationEventPlusOneFormula);
//    BadgeDefinitionReputationEvent dbRepDefnEvent = cacheBadgeDefinitionReputationEventService.getBadgeDefinitionReputationEvent(badgeDefinitionReputationEventPlusOneFormula.getId()).orElseThrow();
//    assertTrue(dbRepDefnEvent.getFormulaEvents().contains(plusOneFormulaEvent));
//    assertEquals(reputationIdentifierTag, dbRepDefnEvent.getIdentifierTag());
//    assertEquals(dbRepDefnEvent.getExternalIdentityTag(), externalIdentityTag);
//    assertEquals(badgeDefinitionReputationEventPlusOneFormula, dbRepDefnEvent);
//
//    assertTrue(dbRepDefnEvent.getFormulaEvents().stream()
//        .map(FormulaEvent::getFormula)
//        .toList().contains(PLUS_ONE_FORMULA));
//
//    assertTrue(dbRepDefnEvent.getFormulaEvents().stream()
//        .map(FormulaEvent::getBadgeDefinitionAwardEvent)
//        .map(BadgeDefinitionAwardEvent::getIdentifierTag)
//        .map(IdentifierTag::getUuid).toList().contains(UNIT_UPVOTE));
//
//    String UNIT_DOWNVOTE = "UNIT_DOWNVOTE";
//    String MINUS_ONE_FORMULA = "-1";
//    IdentifierTag downvoteIdentifierTag = new IdentifierTag(UNIT_DOWNVOTE);
//
//    BadgeDefinitionAwardEvent awardDownvoteDefinitionEvent = new BadgeDefinitionAwardEvent(identity, downvoteIdentifierTag, relay);
//    FormulaEvent minusOneFormulaEvent = new FormulaEvent(identity, awardDownvoteDefinitionEvent, MINUS_ONE_FORMULA);
//
//    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneMinusOne = new BadgeDefinitionReputationEvent(
//        identity,
//        reputationIdentifierTag,
//        relay,
//        externalIdentityTag,
//        List.of(plusOneFormulaEvent, minusOneFormulaEvent));
//
//    String messageDuplicateIdentifierTag = assertThrows(NostrException.class, () -> eventPlugin.processIncomingEvent(badgeDefinitionReputationEventPlusOneMinusOne)).getMessage();
//    assertTrue(
//        messageDuplicateIdentifierTag.contains(
//            String.format(
//                DUPLICATE_REPUTATION_IDENTIFIER_TAG, badgeDefinitionReputationEventPlusOneMinusOne.getId(), reputationIdentifierTag.getUuid(), badgeDefinitionReputationEventPlusOneFormula.getId())));
//
//    IdentifierTag uniqueIdentifierTag = new IdentifierTag("UNIQUE_" + REPUTATION);
//    BadgeDefinitionReputationEvent uniqueBadgeDefinitionReputationEventIdentifierTag = new BadgeDefinitionReputationEvent(
//        identity,
//        uniqueIdentifierTag,
//        relay,
//        externalIdentityTag,
//        List.of(plusOneFormulaEvent, minusOneFormulaEvent));
//
//    String messageMissingEventId = assertThrows(NostrException.class, () -> eventPlugin.processIncomingEvent(uniqueBadgeDefinitionReputationEventIdentifierTag)).getMessage();
//    assertTrue(
//        messageMissingEventId.contains(
//            String.format(
//                Strings.concat("", NON_EXISTENT_EVENT_ID_S, "[%s]"),
//                uniqueBadgeDefinitionReputationEventIdentifierTag.getId(),
//                minusOneFormulaEvent.getId())));
//
//    eventPlugin.processIncomingEvent(awardDownvoteDefinitionEvent);
//    eventPlugin.processIncomingEvent(minusOneFormulaEvent);
//    eventPlugin.processIncomingEvent(uniqueBadgeDefinitionReputationEventIdentifierTag);
//
//    BadgeDefinitionReputationEvent dbRepDefnEventPlusMinus = cacheBadgeDefinitionReputationEventService.getBadgeDefinitionReputationEvent(uniqueBadgeDefinitionReputationEventIdentifierTag.getId()).orElseThrow();
//    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().contains(plusOneFormulaEvent));
//    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().contains(minusOneFormulaEvent));
//    assertEquals(uniqueIdentifierTag, dbRepDefnEventPlusMinus.getIdentifierTag());
//    assertEquals(dbRepDefnEventPlusMinus.getExternalIdentityTag(), externalIdentityTag);
//    assertEquals(uniqueBadgeDefinitionReputationEventIdentifierTag, dbRepDefnEventPlusMinus);
//    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().stream()
//        .map(FormulaEvent::getFormula)
//        .toList().contains(MINUS_ONE_FORMULA));
//    assertTrue(dbRepDefnEventPlusMinus.getFormulaEvents().stream()
//        .map(FormulaEvent::getBadgeDefinitionAwardEvent)
//        .map(BadgeDefinitionAwardEvent::getIdentifierTag)
//        .map(IdentifierTag::getUuid).toList().contains(UNIT_DOWNVOTE));
//  }
//}
