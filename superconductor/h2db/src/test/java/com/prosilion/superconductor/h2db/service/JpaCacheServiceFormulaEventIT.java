package com.prosilion.superconductor.h2db.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.h2db.util.Factory;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.service.JpaCacheServiceIF;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class JpaCacheServiceFormulaEventIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();

  private final JpaCacheServiceIF jpaCacheServiceIF;
  private final FormulaEvent formulaEvent;
  private final Long savedId;

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
  public final Identity identity = Identity.generateRandomIdentity();
  public final String PLUS_ONE_FORMULA = "+1";

  @Autowired
  public JpaCacheServiceFormulaEventIT(JpaCacheServiceIF jpaCacheServiceIF) throws ParseException {
    this.jpaCacheServiceIF = jpaCacheServiceIF;

    this.formulaEvent =
        new FormulaEvent(
            IDENTITY,
            new BadgeDefinitionAwardEvent(
                IDENTITY,
                upvoteIdentifierTag,
                PLUS_ONE_FORMULA),
            PLUS_ONE_FORMULA);

    this.savedId = jpaCacheServiceIF.save(this.formulaEvent);
  }

  @Test
  void testGetByEventId() {
    assertNotNull(savedId);
    log.info("saved id: {}", savedId);

    List<EventJpaEntityIF> all = jpaCacheServiceIF.getAll();

    assertTrue(all.stream()
        .map(EventJpaEntityIF::getUid)
        .anyMatch(savedId::equals));

    EventJpaEntityIF firstRetrievedEventEntityIF = jpaCacheServiceIF.getEventByUid(savedId).orElseThrow();
    assertEquals(savedId, firstRetrievedEventEntityIF.getUid());

    GenericEventKindDto firstDto = new GenericEventKindDto(formulaEvent);
    EventJpaEntityIF firstEntityIF = firstDto.convertDtoToEntity();
    assertEquals(firstEntityIF, firstRetrievedEventEntityIF);

    EventJpaEntityIF secondRetrievedEntityIF = jpaCacheServiceIF.getEventByUid(savedId).orElseThrow();
    assertEquals(savedId, secondRetrievedEntityIF.getUid());

    GenericEventKindDto secondDto = new GenericEventKindDto(formulaEvent);
    EventJpaEntityIF secondEntityIF = secondDto.convertDtoToEntity();

    assertEquals(secondEntityIF, secondRetrievedEntityIF);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testGetByEventIdString() {
    assertNotNull(savedId);
    log.info("saved id: {}", savedId);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .map(EventJpaEntityIF::getUid)
        .anyMatch(savedId::equals));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", savedId);
    log.info("retrieved ids:");
//    all.stream().map(EventEntityIF::getUid).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    EventJpaEntityIF firstRetrieval = jpaCacheServiceIF.getEventByEventId(
        formulaEvent.getId()).orElseThrow();
    assertEquals(savedId, firstRetrieval.getUid());

    EventJpaEntityIF secondRetrieval = jpaCacheServiceIF.getEventByEventId(
        formulaEvent.getId()).orElseThrow();
    assertEquals(savedId, secondRetrieval.getUid());

    assertEquals(firstRetrieval, secondRetrieval);

    GenericEventKindDto firstDto = new GenericEventKindDto(formulaEvent);
    EventJpaEntityIF firstEntity = firstDto.convertDtoToEntity();
//    assertEquals(firstEntity, firstRetrieval);

    GenericEventKindDto secondDto = new GenericEventKindDto(formulaEvent);
    EventJpaEntityIF secondEntity = secondDto.convertDtoToEntity();

    assertEquals(firstEntity, secondEntity);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = jpaCacheServiceIF.getAll().size();
    log.debug("startSize: {}", startSize);

    Long savedUidOfDuplicate = jpaCacheServiceIF.save(formulaEvent);
    assertEquals(savedId, savedUidOfDuplicate);

    int endSize = jpaCacheServiceIF.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() throws ParseException {
    log.info("saved id: {}", savedId);

    List<EventJpaEntityIF> all = jpaCacheServiceIF.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    FormulaEvent eventToDelete =
        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
            IDENTITY,
            upvoteIdentifierTag,
            PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
    Long eventToDeleteUid = jpaCacheServiceIF.save(eventToDelete);

    List<EventJpaEntityIF> allAfterDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .map(EventJpaEntityIF::getUid)
        .anyMatch(eventToDeleteUid::equals));

    EventTag eventTag = new EventTag(eventToDelete.getId());

    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(deletionEvent.getTags().contains(eventTag));

    jpaCacheServiceIF.deleteEvent(deletionEvent);

    List<DeletionEventJpaEntityIF> allDeletionJpaEventEntities = jpaCacheServiceIF.getAllDeletionEvents();
    assertEquals(1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(event -> {
      log.debug("deletionDbId: {}", event.getId().toString());
      log.debug("deletionDbEventId: {}", event.getEventId().toString());
    });

    assertTrue(allDeletionJpaEventEntities.stream().map(DeletionEventJpaEntityIF::getEventId).anyMatch(eventToDeleteUid::equals));

    List<EventJpaEntityIF> allAfterDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterDeletion = allAfterDeletion.size();
    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);

    assertTrue(allAfterDeletion.stream().map(EventJpaEntityIF::getUid).noneMatch(eventToDeleteUid::equals));
    assertTrue(allAfterDeletion.stream().map(EventJpaEntityIF::getId).noneMatch(eventToDelete.getId()::equals));

    deleteSecondEvent(sizeAfterDeletion, allDeletionJpaEventEntities.size(), eventToDelete.getId());
  }

  private void deleteSecondEvent(
      int allEventsSizeAfterFirstDeletion,
      int allDeletedEventsSizeAfterFirstDeletion,
      String firstDeletedEventId) throws ParseException {

    List<EventJpaEntityIF> all = jpaCacheServiceIF.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    FormulaEvent secondEventToDelete =
        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
            IDENTITY,
            upvoteIdentifierTag,
            PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
    Long secondEventToDeleteUid = jpaCacheServiceIF.save(secondEventToDelete);

    List<EventJpaEntityIF> allAfterSecondDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .map(EventJpaEntityIF::getUid)
        .anyMatch(secondEventToDeleteUid::equals));

    EventTag eventTag = new EventTag(secondEventToDelete.getId());

    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(secondDeletionEvent.getTags().contains(eventTag));

    jpaCacheServiceIF.deleteEvent(secondDeletionEvent);

    List<DeletionEventJpaEntityIF> allDeletionJpaEventEntities = jpaCacheServiceIF.getAllDeletionEvents();
    assertEquals(allDeletedEventsSizeAfterFirstDeletion + 1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(event -> {
      log.debug("deletionDbId: {}", event.getId().toString());
      log.debug("deletionDbEventId: {}", event.getEventId().toString());
    });

    assertTrue(allDeletionJpaEventEntities.stream().map(DeletionEventJpaEntityIF::getEventId).anyMatch(secondEventToDeleteUid::equals));

    List<EventJpaEntityIF> allAfterSecondDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

    assertTrue(allAfterSecondDeletion.stream().map(EventJpaEntityIF::getUid).noneMatch(secondEventToDeleteUid::equals));
    assertTrue(allAfterSecondDeletion.stream().map(EventJpaEntityIF::getId).noneMatch(secondEventToDelete.getId()::equals));
    assertTrue(allAfterSecondDeletion.stream().map(EventJpaEntityIF::getId).noneMatch(firstDeletedEventId::equals));
  }
}
