//package com.prosilion.superconductor.redis.service;
//
//import com.ezylang.evalex.parser.ParseException;
//import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.nostr.event.DeletionEvent;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.nostr.event.FormulaEvent;
//import com.prosilion.nostr.event.internal.Relay;
//import com.prosilion.nostr.tag.EventTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.superconductor.lib.redis.dto.GenericNosqlEntityKindDto;
//import com.prosilion.superconductor.lib.redis.service.RedisCacheServiceIF;
//import com.prosilion.superconductor.redis.util.Factory;
//import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertInstanceOf;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Slf4j
//@EmbeddedRedisStandalone
//@SpringBootTest
//@ActiveProfiles("test")
//public class RedisCacheServiceEventNosqlEntityIFIT {
//  private static final Relay relay = new Relay("ws://localhost:5555");
//  private static final Identity IDENTITY = Factory.createNewIdentity();
//
//  private final RedisCacheServiceIF redisCacheService;
//  private final FormulaEvent formulaEvent;
//  private final BaseEvent eventNosqlEntityIFFormulaEvent;
//
//  private final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
//  private final String PLUS_ONE_FORMULA = "+1";
//  private final String AWARD_EVENT_CONTENT = "anything";
//
//  @Autowired
//  public RedisCacheServiceEventNosqlEntityIFIT(RedisCacheServiceIF redisCacheServiceIF) throws ParseException {
//    this.redisCacheService = redisCacheServiceIF;
//
//    this.formulaEvent =
//        new FormulaEvent(
//            IDENTITY,
//            new BadgeDefinitionGenericEvent(
//                IDENTITY, upvoteIdentifierTag, relay, AWARD_EVENT_CONTENT),
//            PLUS_ONE_FORMULA);
//
//    this.eventNosqlEntityIFFormulaEvent = redisCacheServiceIF.save(this.formulaEvent);
//  }
//
//  @Test
//  void testGetBadDefinitionAwardEventFromFormulaEvent() {
//    BaseEvent event = redisCacheService.getEventByEventId(eventNosqlEntityIFFormulaEvent.getId()).orElseThrow();
//    log.info(event.toString());
//    assertInstanceOf(FormulaEvent.class, event);
//  }
//
//  @Test
//  void testGetByEventId() {
//    assertNotNull(eventNosqlEntityIFFormulaEvent);
//    log.info("saved id: {}", eventNosqlEntityIFFormulaEvent);
//
//    List<? extends BaseEvent> all = redisCacheService.getAll();
//
//    assertTrue(all.stream()
//        .map(BaseEvent::getId)
//        .anyMatch(e -> e.equals(eventNosqlEntityIFFormulaEvent.getId())));
//
//    EventIF firstRetrievedEventEntityIF = redisCacheService.getEventByEventId(eventNosqlEntityIFFormulaEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFFormulaEvent.getId(), firstRetrievedEventEntityIF.getId());
//
//    assertEquals(formulaEvent, firstRetrievedEventEntityIF);
//
//    BaseEvent secondRetrievedEntityIF = redisCacheService.getEventByEventId(eventNosqlEntityIFFormulaEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFFormulaEvent.getId(), secondRetrievedEntityIF.getId());
//
//    assertEquals(firstRetrievedEventEntityIF, secondRetrievedEntityIF);
//  }
//
//  @Test
//  void testGetByEventIdString() {
//    assertNotNull(eventNosqlEntityIFFormulaEvent);
//    log.info("saved id: {}", eventNosqlEntityIFFormulaEvent);
//
//    assertTrue(redisCacheService.getAll().stream()
//        .map(BaseEvent::getId)
//        .anyMatch(e -> e.equals(eventNosqlEntityIFFormulaEvent.getId())));
//
//    log.info("********************");
//    log.info("********************");
//    log.info("expicitly saved id: {}", eventNosqlEntityIFFormulaEvent);
//    log.info("retrieved ids:");
////    all.stream().map(BaseEvent::getId).forEach(id -> log.info("  {}", id));
//    log.info("********************");
//    log.info("********************");
//
//    BaseEvent firstRetrieval = redisCacheService.getEventByEventId(formulaEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFFormulaEvent.getId(), firstRetrieval.getId());
//
//    BaseEvent secondRetrieval = redisCacheService.getEventByEventId(formulaEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFFormulaEvent.getId(), secondRetrieval.getId());
//
//    assertEquals(firstRetrieval, secondRetrieval);
//
//    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(formulaEvent);
//    BaseEvent firstEntity = firstDto.baseEvent();
////    assertEquals(firstEntity, firstRetrieval);
//
//    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(formulaEvent);
//    BaseEvent secondEntity = secondDto.baseEvent();
//
//    assertEquals(firstEntity, secondEntity);
//    assertEquals(firstDto, secondDto);
//  }
//
//  @Test
//  void testDuplicateSaveAttempt() {
//    int startSize = redisCacheService.getAll().size();
//    log.debug("startSize: {}", startSize);
//
//    BaseEvent savedUidOfDuplicate = redisCacheService.save(formulaEvent);
//    assertEquals(eventNosqlEntityIFFormulaEvent, savedUidOfDuplicate);
//
//    int endSize = redisCacheService.getAll().size();
//    log.debug("endSize: {}", endSize);
//    assertEquals(startSize, endSize);
//  }
//
//  @Test
//  void testDeletedEvent() throws ParseException {
//    log.info("saved id: {}", eventNosqlEntityIFFormulaEvent);
//
//    List<? extends BaseEvent> all = redisCacheService.getAll();
//    int sizeBeforeDeleteMeEvent = all.size();
//    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);
//
//    FormulaEvent eventToDelete = new FormulaEvent(
//        IDENTITY,
//        new BadgeDefinitionGenericEvent(
//            IDENTITY,
//            upvoteIdentifierTag,
//            relay, 
//            AWARD_EVENT_CONTENT),
//        PLUS_ONE_FORMULA);
//    assertEquals(eventToDelete.getId(), redisCacheService.save(eventToDelete).getId());
//
//    List<? extends BaseEvent> allAfterDeleteMeEvent = redisCacheService.getAll();
//    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
//    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
//    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);
//
//    assertTrue(allAfterDeleteMeEvent.stream()
//        .anyMatch(e -> e.equals(eventToDelete)));
//
//    assertTrue(allAfterDeleteMeEvent.stream()
//        .map(BaseEvent::getId)
//        .anyMatch(e -> e.equals(eventToDelete.getId())));
//
//    List<String> allDeletionJpaEventEntitiesBeforeDeletion = redisCacheService.getAllDeletionEventIds();
//
//    EventTag eventTag = new EventTag(eventToDelete.getId());
//
//    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
//    assertTrue(deletionEvent.getTags().contains(eventTag));
//
//    redisCacheService.deleteEvent(deletionEvent);
//
//    List<String> allDeletionJpaEventIdsAfterDeletion = redisCacheService.getAllDeletionEventIds();
//    assertEquals(allDeletionJpaEventEntitiesBeforeDeletion.size() + 1, allDeletionJpaEventIdsAfterDeletion.size());
//
//    log.debug(allDeletionJpaEventIdsAfterDeletion.toString());
//    allDeletionJpaEventIdsAfterDeletion.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId));
//
//    assertTrue(allDeletionJpaEventIdsAfterDeletion.stream().anyMatch(eventToDelete.getId()::equals));
//
//    List<? extends BaseEvent> allAfterDeletion = redisCacheService.getAll();
//    int sizeAfterDeletion = allAfterDeletion.size();
//    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
//    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);
//
//    assertTrue(allAfterDeletion.stream().map(BaseEvent::getId).noneMatch(e -> e.equals(eventToDelete.getId())));
//    deleteSecondEvent(sizeAfterDeletion, allDeletionJpaEventIdsAfterDeletion.size(), eventToDelete.getId());
//  }
//
//  private void deleteSecondEvent(
//      int allEventsSizeAfterFirstDeletion,
//      int allDeletedEventsSizeAfterFirstDeletion,
//      String firstDeletedEventId) throws ParseException {
//
//    List<? extends BaseEvent> all = redisCacheService.getAll();
//    int sizeBeforeSecondDeleteMeEvent = all.size();
//    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
//    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);
//
//    FormulaEvent secondEventToDelete =
//        new FormulaEvent(IDENTITY, new BadgeDefinitionGenericEvent(
//            IDENTITY, upvoteIdentifierTag, relay, 
//            AWARD_EVENT_CONTENT), PLUS_ONE_FORMULA);
//    assertEquals(secondEventToDelete.getId(), redisCacheService.save(secondEventToDelete).getId());
//
//    List<? extends BaseEvent> allAfterSecondDeleteMeEvent = redisCacheService.getAll();
//    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
//    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
//    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);
//
//    assertTrue(redisCacheService.getAll().stream()
//        .map(BaseEvent::getId)
//        .anyMatch(secondEventToDelete.getId()::equals));
//
//    EventTag eventTag = new EventTag(secondEventToDelete.getId());
//
//    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
//    assertTrue(secondDeletionEvent.getTags().contains(eventTag));
//
//    redisCacheService.deleteEvent(secondDeletionEvent);
//
//    List<String> allDeletionJpaEventEntities = redisCacheService.getAllDeletionEventIds();
//    assertEquals(allDeletedEventsSizeAfterFirstDeletion + 1, allDeletionJpaEventEntities.size());
//
//    log.debug(allDeletionJpaEventEntities.toString());
//    allDeletionJpaEventEntities.forEach(eventId -> {
//      log.debug("deletionDbEventId: {}", eventId);
//    });
//
//    assertTrue(allDeletionJpaEventEntities.stream().anyMatch(secondEventToDelete.getId()::equals));
//
//    List<? extends BaseEvent> allAfterSecondDeletion = redisCacheService.getAll();
//    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
//    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
//    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);
//
//    assertTrue(allAfterSecondDeletion.stream().map(BaseEvent::getId).noneMatch(secondEventToDelete.getId()::equals));
//    assertTrue(allAfterSecondDeletion.stream().map(BaseEvent::getId).noneMatch(firstDeletedEventId::equals));
//  }
//}
