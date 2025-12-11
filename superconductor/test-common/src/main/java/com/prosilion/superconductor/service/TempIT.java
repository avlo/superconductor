//package com.prosilion.superconductor.service;
//
//import com.ezylang.evalex.parser.ParseException;
//import com.prosilion.nostr.NostrException;
//import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
//import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
//import com.prosilion.nostr.event.FormulaEvent;
//import com.prosilion.nostr.event.internal.Relay;
//import com.prosilion.nostr.tag.ExternalIdentityTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeDefinitionReputationEventService;
//import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
//import com.prosilion.superconductor.base.service.CacheTagMappedEventServiceIF;
//import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
//import com.prosilion.superconductor.util.Factory;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.logging.log4j.util.Strings;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//
//import static com.prosilion.superconductor.util.TestKindType.UNIT_REPUTATION;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Slf4j
//public abstract class TempIT {
//  private static final Relay relay = new Relay("ws://localhost:5555");
//  private static final Identity identity = Factory.createNewIdentity();
//
//  private final CacheFormulaEventService cacheFormulaEventService;
//  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;
//  private final EventPluginIF eventPluginIF;
//  private final BadgeDefinitionAwardEvent badgeDefinitionAwardEvent;
//  private final FormulaEvent formulaEvent;
//
//  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
//  public final String PLUS_ONE_FORMULA = "+1";
//
//  private static final String PLATFORM = TempIT.class.getPackageName();
//  private static final String IDENTITY = TempIT.class.getSimpleName();
//  private static final String PROOF = String.valueOf(TempIT.class.hashCode());
//  private final ExternalIdentityTag externalIdentityTag = new ExternalIdentityTag(PLATFORM, IDENTITY, PROOF);
//
//  public TempIT(
//      EventPluginIF eventPluginIF,
//      CacheTagMappedEventServiceIF cacheFormulaEventService,
//      CacheTagMappedEventServiceIF cacheBadgeDefinitionReputationEventService) throws ParseException {
//    this.eventPluginIF = eventPluginIF;
//    this.cacheFormulaEventService = (CacheFormulaEventService) cacheFormulaEventService;
//    this.cacheBadgeDefinitionReputationEventService = (CacheBadgeDefinitionReputationEventService) cacheBadgeDefinitionReputationEventService;
//    this.badgeDefinitionAwardEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
//
//    this.formulaEvent =
//        new FormulaEvent(
//            identity,
//            upvoteIdentifierTag,
//            badgeDefinitionAwardEvent,
//            PLUS_ONE_FORMULA);
//  }
//
//  @Test
//  @Order(1)
//  void testNonExistentEventTag() {
//    assertTrue(
//        assertThrows(NostrException.class, () ->
//            eventPluginIF.processIncomingEvent(this.formulaEvent))
//            .getMessage().contains(
//                Strings.concat(
//                    String.format(CacheFormulaEventService.NON_EXISTENT_EVENT_ID_S, formulaEvent.getId()),
//                    String.format("[%s]", badgeDefinitionAwardEvent.getId()))));
//
//    eventPluginIF.processIncomingEvent(this.badgeDefinitionAwardEvent);
//    eventPluginIF.processIncomingEvent(this.formulaEvent);
//    FormulaEvent savedFormulaEvent = cacheFormulaEventService.getFormulaEvent(formulaEvent.getId()).orElseThrow();
//    assertNotNull(savedFormulaEvent);
//
//    log.info("saved id: {}", savedFormulaEvent);
//
//    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
//        identity,
//        new IdentifierTag(
//            UNIT_REPUTATION.getName()),
//        relay,
//        externalIdentityTag,
//        formulaEvent);
//
//    eventPluginIF.processIncomingEvent(badgeDefinitionReputationEvent);
//    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent1 = cacheBadgeDefinitionReputationEventService.getBadgeDefinitionReputationEvent(badgeDefinitionReputationEvent.getId()).orElseThrow();
//    assertNotNull(badgeDefinitionReputationEvent1);
//  }
//}
