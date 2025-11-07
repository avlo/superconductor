//package com.prosilion.superconductor.redis.service;
//
//import com.ezylang.evalex.parser.ParseException;
//import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
//import com.prosilion.nostr.event.DeletionEvent;
//import com.prosilion.nostr.event.FormulaEvent;
//import com.prosilion.nostr.tag.EventTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.superconductor.lib.redis.dto.GenericNosqlEntityKindDto;
//import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntityIF;
//import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
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
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Slf4j
//@EmbeddedRedisStandalone
//@SpringBootTest
//@ActiveProfiles("test")
//public class RedisCacheServiceBadgeDefinitionAwardEventIT {
//  private static final Identity IDENTITY = Factory.createNewIdentity();
//
//  private final RedisCacheServiceIF redisCacheService;
//  private final BadgeDefinitionAwardEvent badgeDefinitionAwardEvent;
//  private final EventNosqlEntityIF eventNosqlEntityIFBadgeDefinitionAwardEvent;
//
//  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
//  public final String PLUS_ONE_FORMULA = "+1";
//
//  @Autowired
//  public RedisCacheServiceBadgeDefinitionAwardEventIT(RedisCacheServiceIF redisCacheServiceIF) {
//    this.redisCacheService = redisCacheServiceIF;
//    this.badgeDefinitionAwardEvent = new BadgeDefinitionAwardEvent(IDENTITY, upvoteIdentifierTag, PLUS_ONE_FORMULA);
//    this.eventNosqlEntityIFBadgeDefinitionAwardEvent = redisCacheServiceIF.save(this.badgeDefinitionAwardEvent);
//  }
//
//  @Test
//  void testGetByEventId() {
//    assertNotNull(eventNosqlEntityIFBadgeDefinitionAwardEvent);
//    log.info("saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);
//
//    List<EventNosqlEntityIF> all = redisCacheService.getAll();
//
//    assertTrue(all.stream()
//        .map(EventNosqlEntityIF::getId)
//        .anyMatch(e -> e.equals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId())));
//
//    EventNosqlEntityIF firstRetrievedEventEntityIF = redisCacheService.getEventByEventId(eventNosqlEntityIFBadgeDefinitionAwardEvent.getEventId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getEventId(), firstRetrievedEventEntityIF.getId());
//
//    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(badgeDefinitionAwardEvent);
//    EventNosqlEntityIF firstEntityIF = firstDto.convertDtoToNosqlEntity();
////    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded    
//    assertEquals(firstEntityIF, firstRetrievedEventEntityIF);
//
//    EventNosqlEntityIF secondRetrievedEntityIF = redisCacheService.getEventByEventId(eventNosqlEntityIFBadgeDefinitionAwardEvent.getEventId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getEventId(), secondRetrievedEntityIF.getId());
//
//    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(badgeDefinitionAwardEvent);
//    EventNosqlEntityIF secondEntityIF = secondDto.convertDtoToNosqlEntity();
//
////    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded
//    assertEquals(secondEntityIF, secondRetrievedEntityIF);
//    assertEquals(firstDto, secondDto);
//  }
//
//  @Test
//  void testGetByEventIdString() {
//    assertNotNull(eventNosqlEntityIFBadgeDefinitionAwardEvent);
//    log.info("saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);
//
//    assertTrue(redisCacheService.getAll().stream()
//        .map(EventNosqlEntityIF::getId)
//        .anyMatch(e -> e.equals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId())));
//
//    log.info("********************");
//    log.info("********************");
//    log.info("expicitly saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);
//    log.info("retrieved ids:");
////    all.stream().map(EventNosqlEntityIF::getId).forEach(id -> log.info("  {}", id));
//    log.info("********************");
//    log.info("********************");
//
//    EventNosqlEntityIF firstRetrieval = redisCacheService.getEventByEventId(badgeDefinitionAwardEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), firstRetrieval.getId());
//
//    EventNosqlEntityIF secondRetrieval = redisCacheService.getEventByEventId(badgeDefinitionAwardEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), secondRetrieval.getId());
//
//    assertEquals(firstRetrieval, secondRetrieval);
//
//    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(badgeDefinitionAwardEvent);
//    EventNosqlEntityIF firstEntity = firstDto.convertDtoToNosqlEntity();
////    assertEquals(firstEntity, firstRetrieval);
//
//    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(badgeDefinitionAwardEvent);
//    EventNosqlEntityIF secondEntity = secondDto.convertDtoToNosqlEntity();
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
//    EventNosqlEntityIF savedUidOfDuplicate = redisCacheService.save(badgeDefinitionAwardEvent);
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent, savedUidOfDuplicate);
//
//    int endSize = redisCacheService.getAll().size();
//    log.debug("endSize: {}", endSize);
//    assertEquals(startSize, endSize);
//  }
//
//  @Test
//  void testDeletedEvent() throws ParseException {
//    log.info("saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);
//
//    List<EventNosqlEntityIF> all = redisCacheService.getAll();
//    int sizeBeforeDeleteMeEvent = all.size();
//    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);
//
//    FormulaEvent eventToDelete = new FormulaEvent(
//        IDENTITY,
//        new BadgeDefinitionAwardEvent(
//            IDENTITY,
//            upvoteIdentifierTag,
//            PLUS_ONE_FORMULA),
//        PLUS_ONE_FORMULA);
//    assertEquals(eventToDelete.getId(), redisCacheService.save(eventToDelete).getId());
//
//    List<EventNosqlEntityIF> allAfterDeleteMeEvent = redisCacheService.getAll();
//    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
//    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
//    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);
//
//    assertTrue(allAfterDeleteMeEvent.stream()
//        .map(EventNosqlEntityIF::getId)
//        .anyMatch(e -> e.equals(eventToDelete.getId())));
//
//    List<DeletionEventNosqlEntityIF> allDeletionJpaEventEntitiesBeforeDeletion = redisCacheService.getAllDeletionEvents();
//
//    EventTag eventTag = new EventTag(eventToDelete.getId());
//
//    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
//    assertTrue(deletionEvent.getTags().contains(eventTag));
//
//    redisCacheService.deleteEvent(deletionEvent);
//
//    List<DeletionEventNosqlEntityIF> allDeletionJpaEventEntitiesAfterDeletion = redisCacheService.getAllDeletionEvents();
//    assertEquals(allDeletionJpaEventEntitiesBeforeDeletion.size() + 1, allDeletionJpaEventEntitiesAfterDeletion.size());
//
//    log.debug(allDeletionJpaEventEntitiesAfterDeletion.toString());
//    allDeletionJpaEventEntitiesAfterDeletion.forEach(event -> {
//      log.debug("deletionDbId: {}", event.getId());
//      log.debug("deletionDbEventId: {}", event.getEventId());
//    });
//
//    assertTrue(allDeletionJpaEventEntitiesAfterDeletion.stream().map(DeletionEventNosqlEntityIF::getEventId).anyMatch(eventToDelete.getId()::equals));
//
//    List<EventNosqlEntityIF> allAfterDeletion = redisCacheService.getAll();
//    int sizeAfterDeletion = allAfterDeletion.size();
//    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
//    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);
//
//    assertTrue(allAfterDeletion.stream().map(EventNosqlEntityIF::getId).noneMatch(e -> e.equals(eventToDelete.getId())));
//    deleteSecondEvent(sizeAfterDeletion, allDeletionJpaEventEntitiesAfterDeletion.size(), eventToDelete.getId());
//  }
//
//  private void deleteSecondEvent(
//      int allEventsSizeAfterFirstDeletion,
//      int allDeletedEventsSizeAfterFirstDeletion,
//      String firstDeletedEventId) throws ParseException {
//
//    List<EventNosqlEntityIF> all = redisCacheService.getAll();
//    int sizeBeforeSecondDeleteMeEvent = all.size();
//    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
//    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);
//
//    FormulaEvent secondEventToDelete =
//        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
//            IDENTITY, upvoteIdentifierTag, PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
//    assertEquals(secondEventToDelete.getId(), redisCacheService.save(secondEventToDelete).getId());
//
//    List<EventNosqlEntityIF> allAfterSecondDeleteMeEvent = redisCacheService.getAll();
//    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
//    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
//    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);
//
//    assertTrue(redisCacheService.getAll().stream()
//        .map(EventNosqlEntityIF::getId)
//        .anyMatch(secondEventToDelete.getId()::equals));
//
//    EventTag eventTag = new EventTag(secondEventToDelete.getId());
//
//    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
//    assertTrue(secondDeletionEvent.getTags().contains(eventTag));
//
//    redisCacheService.deleteEvent(secondDeletionEvent);
//
//    List<DeletionEventNosqlEntityIF> allDeletionJpaEventEntities = redisCacheService.getAllDeletionEvents();
//    assertEquals(allDeletedEventsSizeAfterFirstDeletion + 1, allDeletionJpaEventEntities.size());
//
//    log.debug(allDeletionJpaEventEntities.toString());
//    allDeletionJpaEventEntities.forEach(event -> {
//      log.debug("deletionDbId: {}", event.getId());
//      log.debug("deletionDbEventId: {}", event.getEventId());
//    });
//
//    assertTrue(allDeletionJpaEventEntities.stream().map(DeletionEventNosqlEntityIF::getEventId).anyMatch(secondEventToDelete.getId()::equals));
//
//    List<EventNosqlEntityIF> allAfterSecondDeletion = redisCacheService.getAll();
//    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
//    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
//    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);
//
//    assertTrue(allAfterSecondDeletion.stream().map(EventNosqlEntityIF::getId).noneMatch(secondEventToDelete.getId()::equals));
//    assertTrue(allAfterSecondDeletion.stream().map(EventNosqlEntityIF::getId).noneMatch(firstDeletedEventId::equals));
//  }
//}
