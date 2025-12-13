package com.prosilion.superconductor.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
public abstract class BaseCacheServiceGenericEventRecordUsingFormulaEventIT {
  public static final Relay relay = new Relay("ws://localhost:5555");
//  private static final Relay relay = new Relay("ws://localhost:5555");
//  private static final Identity IDENTITY = Factory.createNewIdentity();

  private final EventPluginIF eventPluginIF;
  private final BadgeDefinitionAwardEvent badgeDefinitionAwardEvent;
  private final FormulaEvent formulaEvent;

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
  public final String PLUS_ONE_FORMULA = "+1";

  public BaseCacheServiceGenericEventRecordUsingFormulaEventIT(
      EventPluginIF eventPluginIF,
      @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull Identity superconductorInstanceIdentity) throws ParseException {
    this.eventPluginIF = eventPluginIF;
    this.badgeDefinitionAwardEvent = badgeDefinitionUpvoteEvent;

    this.formulaEvent =
        new FormulaEvent(
            superconductorInstanceIdentity,
            upvoteIdentifierTag,
            relay,
            badgeDefinitionAwardEvent,
            PLUS_ONE_FORMULA);
  }

  @Test
  @Order(10)
  void testSaveFormulaEvent() {
    eventPluginIF.processIncomingEvent(this.formulaEvent);
  }

//  @Test
//  @Order(20)
//  void testDuplicateFormulaEventWithDifferentContent() {
//    assertTrue(
//        assertThrows(NostrException.class, () ->
//            eventPluginIF.processIncomingEvent(this.formulaEvent))
//            .getMessage().contains(
//                Strings.concat(
//                    String.format(CacheFormulaEventService.NON_EXISTENT_EVENT_ID_S, formulaEvent.getId()),
//                    String.format("[%s]", badgeDefinitionAwardEvent.getId()))));
//  }
//
//  @Test
//  @Order(30)
//  void testExistentEventTag() throws ParseException {
//    eventPluginIF.processIncomingEvent(this.badgeDefinitionAwardEvent);
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
//        IDENTITY,
//        badgeDefinitionAwardEvent,
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
//    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
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
//        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
//            IDENTITY,
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
//    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
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
}
