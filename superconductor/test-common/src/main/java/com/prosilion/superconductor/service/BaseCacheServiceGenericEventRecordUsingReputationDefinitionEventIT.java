//package com.prosilion.superconductor.service;
//
//import com.ezylang.evalex.parser.ParseException;
//import com.prosilion.nostr.NostrException;
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
//import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
//import com.prosilion.nostr.event.DeletionEvent;
//import com.prosilion.nostr.event.FormulaEvent;
//import com.prosilion.nostr.event.internal.Relay;
//import com.prosilion.nostr.tag.EventTag;
//import com.prosilion.nostr.tag.ExternalIdentityTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
//import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
//import com.prosilion.superconductor.util.Factory;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.logging.log4j.util.Strings;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//
//import static com.prosilion.superconductor.util.TestKindType.UNIT_REPUTATION;
//import static com.prosilion.superconductor.util.TestKindType.UNIT_UPVOTE;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Slf4j
//public abstract class BaseCacheServiceGenericEventRecordUsingReputationDefinitionEventIT {
//  private static final Relay relay = new Relay("ws://localhost:5555");
//  private static final Identity authorIdentity = Identity.generateRandomIdentity();
//
//  private static final String PLATFORM = BadgeDefinitionReputationEvent.class.getPackageName();
//  private static final String IDENTITY = BadgeDefinitionReputationEvent.class.getSimpleName();
//  private static final String PROOF = String.valueOf(BadgeDefinitionReputationEvent.class.hashCode());
//
//  private final String PLUS_ONE_FORMULA = "+1";
//  private final IdentifierTag upvoteIdentifierTag = new IdentifierTag(UNIT_UPVOTE.getName());
//  private final BadgeDefinitionAwardEvent badgeDefinitionAwardUpvoteEvent = new BadgeDefinitionAwardEvent(authorIdentity, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
//
//  private final ExternalIdentityTag externalIdentityTag = new ExternalIdentityTag(PLATFORM, IDENTITY, PROOF);
//
//  private final EventPluginIF eventPluginIF;
//  private final FormulaEvent formulaEvent;
//  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEvent;
//
//
//  public BaseCacheServiceGenericEventRecordUsingReputationDefinitionEventIT(EventPluginIF eventPluginIF) throws ParseException {
//    this.eventPluginIF = eventPluginIF;
//
//    this.formulaEvent =
//        new FormulaEvent(
//            authorIdentity,
//            badgeDefinitionAwardUpvoteEvent,
//            PLUS_ONE_FORMULA);
//
//    this.badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
//        authorIdentity,
//        new IdentifierTag(
//            UNIT_REPUTATION.getName()),
//        relay,
//        externalIdentityTag,
//        List.of(
//            formulaEvent));
//  }
//
//  @Test
//  @Order(0)
//  void testNonExistentEventTag() {
//    assertTrue(
//        assertThrows(NostrException.class, () ->
//            eventPluginIF.processIncomingEvent(this.formulaEvent))
//            .getMessage().contains(
//                Strings.concat(
//                    String.format(CacheFormulaEventService.NON_EXISTENT_EVENT_ID_S, formulaEvent.getId()),
//                    String.format("[%s]", badgeDefinitionAwardUpvoteEvent.getId()))));
//  }
//
//  @Test
//  @Order(1)
//  void testExistentEventTag() throws ParseException {
//    eventPluginIF.processIncomingEvent(this.badgeDefinitionAwardUpvoteEvent);
//    eventPluginIF.processIncomingEvent(this.formulaEvent);
//    FormulaEvent savedFormulaEvent = cacheFormulaEventService.getFormulaEvent(formulaEvent.getId()).orElseThrow();
//    assertNotNull(savedFormulaEvent);
//
//    log.info("saved id: {}", savedFormulaEvent);
//
//    List<FormulaEvent> all = cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA);
//
//    assertTrue(all.stream()
//        .map(FormulaEvent::getId)
//        .anyMatch(e -> e.equals(savedFormulaEvent.getId())));
//
//    FormulaEvent firstRetrievedEventEntityIF = cacheFormulaEventService.getFormulaEvent(savedFormulaEvent.getId()).orElseThrow();
//    assertEquals(savedFormulaEvent.getId(), firstRetrievedEventEntityIF.getId());
//    assertEquals(savedFormulaEvent, firstRetrievedEventEntityIF);
//
//    FormulaEvent secondRetrievedEntityIF = cacheFormulaEventService.getFormulaEvent(savedFormulaEvent.getId()).orElseThrow();
//    assertEquals(savedFormulaEvent.getId(), secondRetrievedEntityIF.getId());
//    assertEquals(savedFormulaEvent, secondRetrievedEntityIF);
//    assertEquals(firstRetrievedEventEntityIF, secondRetrievedEntityIF);
//
//    assertNotNull(savedFormulaEvent);
//    log.info("saved id: {}", savedFormulaEvent);
//
//    Assertions.assertTrue(cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA).stream()
//        .map(FormulaEvent::getId)
//        .anyMatch(e -> e.equals(savedFormulaEvent.getId())));
//
//    log.info("********************");
//    log.info("********************");
//    log.info("expicitly saved id: {}", savedFormulaEvent);
//    log.info("retrieved ids:");
////    all.stream().map(FormulaEvent::getId).forEach(id -> log.info("  {}", id));
//    log.info("********************");
//    log.info("********************");
//
//    FormulaEvent firstRetrieval = cacheFormulaEventService.getFormulaEvent(savedFormulaEvent.getId()).orElseThrow();
//    assertEquals(savedFormulaEvent.getId(), firstRetrieval.getId());
//
//    FormulaEvent secondRetrieval = cacheFormulaEventService.getFormulaEvent(savedFormulaEvent.getId()).orElseThrow();
//    assertEquals(savedFormulaEvent.getId(), secondRetrieval.getId());
//    assertEquals(firstRetrieval, secondRetrieval);
//
//    int startSize = cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA).size();
//    log.debug("startSize: {}", startSize);
//
//    cacheFormulaEventService.save(savedFormulaEvent);
//    FormulaEvent savedUidOfDuplicate = cacheFormulaEventService.getFormulaEvent(savedFormulaEvent.getId()).orElseThrow();
//    assertEquals(savedFormulaEvent, savedUidOfDuplicate);
//
//    int endSize = cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA).size();
//    log.debug("endSize: {}", endSize);
//    assertEquals(startSize, endSize);
//
//    log.info("saved id: {}", savedFormulaEvent);
//
//    all = cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA);
//    int sizeBeforeDeleteMeEvent = all.size();
//    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);
//
//    FormulaEvent eventToDelete = new FormulaEvent(
//        authorIdentity,
//        badgeDefinitionAwardUpvoteEvent,
//        PLUS_ONE_FORMULA);
//    eventPluginIF.processIncomingEvent(eventToDelete);
//    Assertions.assertEquals(eventToDelete, cacheFormulaEventService.getFormulaEvent(eventToDelete.getId()).orElseThrow());
//
//    List<FormulaEvent> allAfterDeleteMeEvent = cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA);
//    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
//    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
//    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);
//
//    assertTrue(allAfterDeleteMeEvent.stream().anyMatch(eventToDelete::equals));
//
//    assertTrue(allAfterDeleteMeEvent.stream()
//        .map(FormulaEvent::getId)
//        .anyMatch(e -> e.equals(eventToDelete.getId())));
//
//    EventTag eventTag = new EventTag(eventToDelete.getId());
//
//    DeletionEvent deletionEvent = new DeletionEvent(authorIdentity, List.of(eventTag), Factory.lorumIpsum());
//    assertTrue(deletionEvent.getTags().contains(eventTag));
//
//    cacheFormulaEventService.deleteEvent(deletionEvent);
//
//    List<FormulaEvent> allAfterDeletion = cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA);
//    int sizeAfterDeletion = allAfterDeletion.size();
//    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
//    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);
//
//    assertTrue(allAfterDeletion.stream().map(FormulaEvent::getId).noneMatch(e -> e.equals(eventToDelete.getId())));
//  }
//
//  private void deleteSecondEvent(
//      int allEventsSizeAfterFirstDeletion,
//      int allDeletedEventsSizeAfterFirstDeletion,
//      String firstDeletedEventId) throws ParseException {
//
//    List<FormulaEvent> all = cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA);
//    int sizeBeforeSecondDeleteMeEvent = all.size();
//    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
//    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);
//
//    FormulaEvent secondEventToDelete =
//        new FormulaEvent(authorIdentity, new BadgeDefinitionAwardEvent(
//            authorIdentity,
//            upvoteIdentifierTag,
//            relay,
//            PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
//
//    cacheFormulaEventService.save(secondEventToDelete);
//    FormulaEvent secondBaseEventToDelete = cacheFormulaEventService.getFormulaEvent(secondEventToDelete.getId()).orElseThrow();
//    assertEquals(secondEventToDelete, secondBaseEventToDelete);
//
//    List<FormulaEvent> allAfterSecondDeleteMeEvent = cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA);
//    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
//    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
//    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);
//
//    Assertions.assertTrue(cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA).stream()
//        .anyMatch(secondBaseEventToDelete::equals));
//
//    EventTag eventTag = new EventTag(secondEventToDelete.getId());
//
//    DeletionEvent secondDeletionEvent = new DeletionEvent(authorIdentity, List.of(eventTag), Factory.lorumIpsum());
//    assertTrue(secondDeletionEvent.getTags().contains(eventTag));
//
//    cacheFormulaEventService.deleteEvent(secondDeletionEvent);
//
//    List<FormulaEvent> allAfterSecondDeletion = cacheFormulaEventService.getFormulaEvents(Kind.ARBITRARY_CUSTOM_APP_DATA);
//    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
//    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
//    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);
//
////    assertTrue(allAfterSecondDeletion.stream().noneMatch(secondBaseEventToDelete::equals));
//    assertTrue(allAfterSecondDeletion.stream().map(FormulaEvent::getId).noneMatch(secondEventToDelete.getId()::equals));
//    assertTrue(allAfterSecondDeletion.stream().map(FormulaEvent::getId).noneMatch(firstDeletedEventId::equals));
//  }
//}
