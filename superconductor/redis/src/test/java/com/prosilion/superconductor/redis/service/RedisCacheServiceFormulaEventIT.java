package com.prosilion.superconductor.redis.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.lib.redis.dto.GenericNosqlEntityKindDto;
import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.service.RedisCacheServiceIF;
import com.prosilion.superconductor.redis.util.Factory;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
public class RedisCacheServiceFormulaEventIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();

  private final RedisCacheServiceIF redisCacheService;
  private final FormulaEvent formulaEvent;
  private final EventNosqlEntityIF eventNosqlEntityIF;

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
  public final String PLUS_ONE_FORMULA = "+1";

  @Autowired
  public RedisCacheServiceFormulaEventIT(RedisCacheServiceIF redisCacheServiceIF) throws ParseException {
    this.redisCacheService = redisCacheServiceIF;

    this.formulaEvent =
        new FormulaEvent(
            IDENTITY,
            new BadgeDefinitionAwardEvent(
                IDENTITY, upvoteIdentifierTag, PLUS_ONE_FORMULA),
            PLUS_ONE_FORMULA);

    this.eventNosqlEntityIF = redisCacheServiceIF.save(this.formulaEvent);
  }

  @Test
  void testGetByEventId() {
    assertNotNull(eventNosqlEntityIF);
    log.info("saved id: {}", eventNosqlEntityIF);

    List<EventNosqlEntityIF> all = redisCacheService.getAll();

    assertTrue(all.stream()
        .map(EventNosqlEntityIF::getId)
        .anyMatch(e -> e.equals(eventNosqlEntityIF.getId())));

    EventNosqlEntityIF firstRetrievedEventEntityIF = redisCacheService.getEventByEventId(eventNosqlEntityIF.getEventId()).orElseThrow();
    assertEquals(eventNosqlEntityIF.getEventId(), firstRetrievedEventEntityIF.getId());

    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(formulaEvent);
    EventNosqlEntityIF firstEntityIF = firstDto.convertDtoToNosqlEntity();
//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded    
    assertEquals(firstEntityIF, firstRetrievedEventEntityIF);

    EventNosqlEntityIF secondRetrievedEntityIF = redisCacheService.getEventByEventId(eventNosqlEntityIF.getEventId()).orElseThrow();
    assertEquals(eventNosqlEntityIF.getEventId(), secondRetrievedEntityIF.getId());

    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(formulaEvent);
    EventNosqlEntityIF secondEntityIF = secondDto.convertDtoToNosqlEntity();

//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded
    assertEquals(secondEntityIF, secondRetrievedEntityIF);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testGetByEventIdString() {
    assertNotNull(eventNosqlEntityIF);
    log.info("saved id: {}", eventNosqlEntityIF);

    assertTrue(redisCacheService.getAll().stream()
        .map(EventNosqlEntityIF::getId)
        .anyMatch(e -> e.equals(eventNosqlEntityIF.getId())));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", eventNosqlEntityIF);
    log.info("retrieved ids:");
//    all.stream().map(EventNosqlEntityIF::getId).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    EventNosqlEntityIF firstRetrieval = redisCacheService.getEventByEventId(formulaEvent.getId()).orElseThrow();
    assertEquals(eventNosqlEntityIF.getId(), firstRetrieval.getId());

    EventNosqlEntityIF secondRetrieval = redisCacheService.getEventByEventId(formulaEvent.getId()).orElseThrow();
    assertEquals(eventNosqlEntityIF.getId(), secondRetrieval.getId());

    assertEquals(firstRetrieval, secondRetrieval);

    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(formulaEvent);
    EventNosqlEntityIF firstEntity = firstDto.convertDtoToNosqlEntity();
//    assertEquals(firstEntity, firstRetrieval);

    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(formulaEvent);
    EventNosqlEntityIF secondEntity = secondDto.convertDtoToNosqlEntity();

    assertEquals(firstEntity, secondEntity);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = redisCacheService.getAll().size();
    log.debug("startSize: {}", startSize);

    EventNosqlEntityIF savedUidOfDuplicate = redisCacheService.save(formulaEvent);
    assertEquals(eventNosqlEntityIF, savedUidOfDuplicate);

    int endSize = redisCacheService.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() throws ParseException {
    log.info("saved id: {}", eventNosqlEntityIF);

    List<EventNosqlEntityIF> all = redisCacheService.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    FormulaEvent eventToDelete = new FormulaEvent(
        IDENTITY,
        new BadgeDefinitionAwardEvent(
            IDENTITY,
            upvoteIdentifierTag,
            PLUS_ONE_FORMULA),
        PLUS_ONE_FORMULA);
    assertEquals(eventToDelete.getId(), redisCacheService.save(eventToDelete).getId());

    List<EventNosqlEntityIF> allAfterDeleteMeEvent = redisCacheService.getAll();
    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);

    assertTrue(allAfterDeleteMeEvent.stream()
        .map(EventNosqlEntityIF::getId)
        .anyMatch(e -> e.equals(eventToDelete.getId())));

    List<DeletionEventNosqlEntityIF> allDeletionJpaEventEntitiesBeforeDeletion = redisCacheService.getAllDeletionEvents();

    EventTag eventTag = new EventTag(eventToDelete.getId());

    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(deletionEvent.getTags().contains(eventTag));

    redisCacheService.deleteEvent(deletionEvent);

    List<DeletionEventNosqlEntityIF> allDeletionJpaEventEntitiesAfterDeletion = redisCacheService.getAllDeletionEvents();
    assertEquals(allDeletionJpaEventEntitiesBeforeDeletion.size() + 1, allDeletionJpaEventEntitiesAfterDeletion.size());

    log.debug(allDeletionJpaEventEntitiesAfterDeletion.toString());
    allDeletionJpaEventEntitiesAfterDeletion.forEach(event -> {
      log.debug("deletionDbId: {}", event.getId());
      log.debug("deletionDbEventId: {}", event.getEventId());
    });

    assertTrue(allDeletionJpaEventEntitiesAfterDeletion.stream().map(DeletionEventNosqlEntityIF::getEventId).anyMatch(eventToDelete.getId()::equals));

    List<EventNosqlEntityIF> allAfterDeletion = redisCacheService.getAll();
    int sizeAfterDeletion = allAfterDeletion.size();
    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);

    assertTrue(allAfterDeletion.stream().map(EventNosqlEntityIF::getId).noneMatch(e -> e.equals(eventToDelete.getId())));
    deleteSecondEvent(sizeAfterDeletion, allDeletionJpaEventEntitiesAfterDeletion.size(), eventToDelete.getId());
  }

  private void deleteSecondEvent(
      int allEventsSizeAfterFirstDeletion,
      int allDeletedEventsSizeAfterFirstDeletion,
      String firstDeletedEventId) throws ParseException {

    List<EventNosqlEntityIF> all = redisCacheService.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    FormulaEvent secondEventToDelete =
        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
            IDENTITY, upvoteIdentifierTag, PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
    assertEquals(secondEventToDelete.getId(), redisCacheService.save(secondEventToDelete).getId());

    List<EventNosqlEntityIF> allAfterSecondDeleteMeEvent = redisCacheService.getAll();
    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);

    assertTrue(redisCacheService.getAll().stream()
        .map(EventNosqlEntityIF::getId)
        .anyMatch(secondEventToDelete.getId()::equals));

    EventTag eventTag = new EventTag(secondEventToDelete.getId());

    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(secondDeletionEvent.getTags().contains(eventTag));

    redisCacheService.deleteEvent(secondDeletionEvent);

    List<DeletionEventNosqlEntityIF> allDeletionJpaEventEntities = redisCacheService.getAllDeletionEvents();
    assertEquals(allDeletedEventsSizeAfterFirstDeletion + 1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(event -> {
      log.debug("deletionDbId: {}", event.getId());
      log.debug("deletionDbEventId: {}", event.getEventId());
    });

    assertTrue(allDeletionJpaEventEntities.stream().map(DeletionEventNosqlEntityIF::getEventId).anyMatch(secondEventToDelete.getId()::equals));

    List<EventNosqlEntityIF> allAfterSecondDeletion = redisCacheService.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

    assertTrue(allAfterSecondDeletion.stream().map(EventNosqlEntityIF::getId).noneMatch(secondEventToDelete.getId()::equals));
    assertTrue(allAfterSecondDeletion.stream().map(EventNosqlEntityIF::getId).noneMatch(firstDeletedEventId::equals));
  }
}
