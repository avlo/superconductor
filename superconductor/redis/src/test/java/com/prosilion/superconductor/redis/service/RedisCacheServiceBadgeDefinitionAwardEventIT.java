//package com.prosilion.superconductor.redis.service;
//
//import com.ezylang.evalex.parser.ParseException;
//import com.prosilion.nostr.NostrException;
//import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.nostr.event.DeletionEvent;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.nostr.event.FormulaEvent;
//import com.prosilion.nostr.event.internal.Relay;
//import com.prosilion.nostr.tag.EventTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
//import com.prosilion.superconductor.lib.redis.dto.GenericNosqlEntityKindDto;
//import com.prosilion.superconductor.redis.util.Factory;
//import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.lang.NonNull;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Slf4j
//@EmbeddedRedisStandalone
//@SpringBootTest
//@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class RedisCacheServiceBadgeDefinitionAwardEventIT {
//  public static final Relay relay = new Relay("ws://localhost:5555");
//  private static final Identity IDENTITY = Factory.createNewIdentity();
//
//  private final CacheFormulaEventService cacheFormulaEventService;
//  private final BadgeDefinitionAwardEvent badgeDefinitionAwardEvent;
//
//  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
//  public final String PLUS_ONE_FORMULA = "+1";
//  
//  private BaseEvent eventNosqlEntityIFBadgeDefinitionAwardEvent;
//
//  @Autowired
//  public RedisCacheServiceBadgeDefinitionAwardEventIT(CacheFormulaEventService cacheFormulaEventService) {
//    this.cacheFormulaEventService = cacheFormulaEventService;
//    this.badgeDefinitionAwardEvent = new BadgeDefinitionAwardEvent(IDENTITY, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
//  }
//
//  @Test
//  @Order(1)
//  void testNonExistentEventTag() {
//    assertThrows(NostrException.class, () -> cacheFormulaEventService.save(this.badgeDefinitionAwardEvent));
//    this.eventNosqlEntityIFBadgeDefinitionAwardEvent = redisCacheServiceIF.save(this.badgeDefinitionAwardEvent);
//
//  }
//
//  @Test
//  @Order(2)
//  void testExistentEventTag() {
//    cacheFormulaEventService.save(this.badgeDefinitionAwardEvent);
//    cacheFormulaEventService.save(this.badgeDefinitionAwardEvent);
//    this.eventNosqlEntityIFBadgeDefinitionAwardEvent = cacheFormulaEventService.save(this.badgeDefinitionAwardEvent);
//
//  }
//  
//  @Test
//  @Order(3)
//  void testGetByEventId() {
//    assertNotNull(eventNosqlEntityIFBadgeDefinitionAwardEvent);
//    log.info("saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);
//
//    List<FormulaEvent> all = cacheFormulaEventService.getAll();
//
//    assertTrue(all.stream()
//        .map(BaseEvent::getId)
//        .anyMatch(e -> e.equals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId())));
//
//    EventIF firstRetrievedEventEntityIF = cacheFormulaEventService.getEventByEventId(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), firstRetrievedEventEntityIF.getId());
//
//    assertEquals(badgeDefinitionAwardEvent, firstRetrievedEventEntityIF);
//
//    EventIF secondRetrievedEntityIF = cacheFormulaEventService.getEventByEventId(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), secondRetrievedEntityIF.getId());
//
////    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded
//    assertEquals(firstRetrievedEventEntityIF, secondRetrievedEntityIF);
//  }
//
//  @Test
//  void testGetByEventIdString() {
//    assertNotNull(eventNosqlEntityIFBadgeDefinitionAwardEvent);
//    log.info("saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);
//
//    assertTrue(cacheFormulaEventService.getAll().stream()
//        .map(EventIF::getId)
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
//    EventIF firstRetrieval = cacheFormulaEventService.getEventByEventId(badgeDefinitionAwardEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), firstRetrieval.getId());
//
//    EventIF secondRetrieval = cacheFormulaEventService.getEventByEventId(badgeDefinitionAwardEvent.getId()).orElseThrow();
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), secondRetrieval.getId());
//
//    assertEquals(firstRetrieval, secondRetrieval);
//
//    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(badgeDefinitionAwardEvent);
//    EventIF firstEntity = firstDto.convertDtoToNosqlEntity();
////    assertEquals(firstEntity, firstRetrieval);
//
//    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(badgeDefinitionAwardEvent);
//    EventIF secondEntity = secondDto.convertDtoToNosqlEntity();
//
//    assertEquals(firstEntity, secondEntity);
//    assertEquals(firstDto, secondDto);
//  }
//
//  @Test
//  void testDuplicateSaveAttempt() {
//    int startSize = cacheFormulaEventService.getAll().size();
//    log.debug("startSize: {}", startSize);
//
//    EventIF savedUidOfDuplicate = cacheFormulaEventService.save(badgeDefinitionAwardEvent);
//    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent, savedUidOfDuplicate);
//
//    int endSize = cacheFormulaEventService.getAll().size();
//    log.debug("endSize: {}", endSize);
//    assertEquals(startSize, endSize);
//  }
//
//  @Test
//  void testDeletedEvent() throws ParseException {
//    log.info("saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);
//
//    List<FormulaEvent> all = cacheFormulaEventService.getAll();
//    int sizeBeforeDeleteMeEvent = all.size();
//    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);
//
//    FormulaEvent eventToDelete = new FormulaEvent(
//        IDENTITY,
//        new BadgeDefinitionAwardEvent(
//            IDENTITY,
//            upvoteIdentifierTag,
//            relay,
//            PLUS_ONE_FORMULA),
//        PLUS_ONE_FORMULA);
//    assertEquals(eventToDelete.getId(), cacheFormulaEventService.save(eventToDelete).getId());
//
//    List<FormulaEvent> allAfterDeleteMeEvent = cacheFormulaEventService.getAll();
//    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
//    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
//    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);
//
//    assertTrue(allAfterDeleteMeEvent.stream()
//        .map(EventIF::getId)
//        .anyMatch(e -> e.equals(eventToDelete.getId())));
//
//    EventTag eventTag = new EventTag(eventToDelete.getId());
//
//    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
//    assertTrue(deletionEvent.getTags().contains(eventTag));
//
//    cacheFormulaEventService.deleteEvent(deletionEvent);
//
//    List<FormulaEvent> allAfterDeletion = cacheFormulaEventService.getAll();
//    int sizeAfterDeletion = allAfterDeletion.size();
//    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
//    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);
//
//    assertTrue(allAfterDeletion.stream().map(EventIF::getId).noneMatch(e -> e.equals(eventToDelete.getId())));
//    deleteSecondEvent(sizeAfterDeletion, eventToDelete.getId());
//  }
//
//  private void deleteSecondEvent(
//      int allEventsSizeAfterFirstDeletion,
//      String firstDeletedEventId) throws ParseException {
//
//    List<FormulaEvent> all = cacheFormulaEventService.getAll();
//    int sizeBeforeSecondDeleteMeEvent = all.size();
//    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
//    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);
//
//    FormulaEvent secondEventToDelete =
//        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
//            IDENTITY, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
//    assertEquals(secondEventToDelete.getId(), cacheFormulaEventService.save(secondEventToDelete).getId());
//
//    List<FormulaEvent> allAfterSecondDeleteMeEvent = cacheFormulaEventService.getAll();
//    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
//    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
//    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);
//
//    assertTrue(cacheFormulaEventService.getAll().stream()
//        .map(EventIF::getId)
//        .anyMatch(secondEventToDelete.getId()::equals));
//
//    EventTag eventTag = new EventTag(secondEventToDelete.getId());
//
//    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
//    assertTrue(secondDeletionEvent.getTags().contains(eventTag));
//
//    cacheFormulaEventService.deleteEvent(secondDeletionEvent);
//
//    List<FormulaEvent> allAfterSecondDeletion = cacheFormulaEventService.getAll();
//    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
//    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
//    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);
//
//    assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(secondEventToDelete.getId()::equals));
//    assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(firstDeletedEventId::equals));
//  }
//}
