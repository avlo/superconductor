package com.prosilion.superconductor.h2db.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.h2db.util.Factory;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import com.prosilion.superconductor.lib.jpa.service.JpaCacheServiceIF;
import java.util.List;
import java.util.Optional;
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
  public static final Relay relay = new Relay("ws://localhost:5555");
  private static final Identity IDENTITY = Factory.createNewIdentity();

  private final JpaCacheServiceIF jpaCacheServiceIF;
  private final FormulaEvent formulaEvent;
  private final BaseEvent savedBaseEvent;

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
                relay,
                PLUS_ONE_FORMULA),
            PLUS_ONE_FORMULA);

    this.savedBaseEvent = jpaCacheServiceIF.save(this.formulaEvent);
  }

  @Test
  void testGetByEventId() {
    assertNotNull(savedBaseEvent);
    log.info("saved id: {}", savedBaseEvent);

    List<? extends BaseEvent> all = jpaCacheServiceIF.getAll();

    assertTrue(all.stream()
        .anyMatch(savedBaseEvent::equals));

    BaseEvent firstRetrievedEventEntityIF = jpaCacheServiceIF.getEvent(savedBaseEvent).orElseThrow();
    assertEquals(savedBaseEvent, firstRetrievedEventEntityIF);

    GenericEventKindDto firstDto = new GenericEventKindDto(formulaEvent);
    BaseEvent firstEntityIF = firstDto.baseEvent();
    assertEquals(firstEntityIF, firstRetrievedEventEntityIF);

    BaseEvent secondRetrievedEntityIF = jpaCacheServiceIF.getEventByEventId(savedBaseEvent.getId()).orElseThrow();
    assertEquals(savedBaseEvent, secondRetrievedEntityIF);

    GenericEventKindDto secondDto = new GenericEventKindDto(formulaEvent);
    BaseEvent secondEntityIF = secondDto.baseEvent();

    assertEquals(secondEntityIF, secondRetrievedEntityIF);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testGetByEventIdString() {
    assertNotNull(savedBaseEvent);
    log.info("saved id: {}", savedBaseEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .anyMatch(savedBaseEvent::equals));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", savedBaseEvent);
    log.info("retrieved ids:");
//    all.stream().map(EventEntityIF::getUid).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    BaseEvent firstRetrieval = jpaCacheServiceIF.getEventByEventId(
        formulaEvent.getId()).orElseThrow();
    assertEquals(savedBaseEvent, firstRetrieval);

    BaseEvent secondRetrieval = jpaCacheServiceIF.getEventByEventId(
        formulaEvent.getId()).orElseThrow();
    assertEquals(savedBaseEvent, secondRetrieval);

    assertEquals(firstRetrieval, secondRetrieval);

    GenericEventKindDto firstDto = new GenericEventKindDto(formulaEvent);
//    BaseEvent firstEntity = firstDto.convertDtoToEntity();
//    assertEquals(firstEntity, firstRetrieval);

    GenericEventKindDto secondDto = new GenericEventKindDto(formulaEvent);
//    BaseEvent secondEntity = secondDto.convertDtoToEntity();

    assertEquals(firstDto, secondDto);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = jpaCacheServiceIF.getAll().size();
    log.debug("startSize: {}", startSize);

    BaseEvent savedUidOfDuplicate = jpaCacheServiceIF.save(formulaEvent);
    assertEquals(savedBaseEvent, savedUidOfDuplicate);

    int endSize = jpaCacheServiceIF.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() throws ParseException {
    log.info("saved id: {}", savedBaseEvent);

    List<? extends BaseEvent> all = jpaCacheServiceIF.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    FormulaEvent eventToDelete =
        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
            IDENTITY,
            upvoteIdentifierTag,
            relay,
            PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
    assertEquals(eventToDelete.getId(), jpaCacheServiceIF.save(eventToDelete).getId());

    List<? extends BaseEvent> allAfterDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);

    assertTrue(allAfterDeleteMeEvent.stream()
        .anyMatch(e -> e.equals(eventToDelete)));

    assertTrue(allAfterDeleteMeEvent.stream()
        .map(BaseEvent::getId)
        .anyMatch(e -> e.equals(eventToDelete.getId())));

    List<Long> allDeletionJpaEventEntitiesBeforeDeletion = jpaCacheServiceIF.getAllDeletionEventIds();

    EventTag eventTag = new EventTag(eventToDelete.getId());

    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(deletionEvent.getTags().contains(eventTag));

    jpaCacheServiceIF.deleteEvent(deletionEvent);

    List<Long> allDeletionJpaEventIdsAfterDeletion = jpaCacheServiceIF.getAllDeletionEventIds();
    assertEquals(allDeletionJpaEventEntitiesBeforeDeletion.size() + 1, allDeletionJpaEventIdsAfterDeletion.size());

    log.debug(allDeletionJpaEventIdsAfterDeletion.toString());
    allDeletionJpaEventIdsAfterDeletion.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId));

    List<? extends BaseEvent> allAfterDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterDeletion = allAfterDeletion.size();
    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);

    assertTrue(allAfterDeletion.stream().map(BaseEvent::getId).noneMatch(e -> e.equals(eventToDelete.getId())));
    deleteSecondEvent(sizeAfterDeletion, allDeletionJpaEventIdsAfterDeletion.size(), eventToDelete.getId());
  }

  private void deleteSecondEvent(
      int allEventsSizeAfterFirstDeletion,
      int allDeletedEventsSizeAfterFirstDeletion,
      String firstDeletedEventId) throws ParseException {

    List<? extends BaseEvent> all = jpaCacheServiceIF.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    FormulaEvent secondEventToDelete =
        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
            IDENTITY,
            upvoteIdentifierTag,
            relay,
            PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
    BaseEvent secondBaseEventToDelete = jpaCacheServiceIF.save(secondEventToDelete);

    List<? extends BaseEvent> allAfterSecondDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .anyMatch(secondBaseEventToDelete::equals));

    EventTag eventTag = new EventTag(secondEventToDelete.getId());

    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(secondDeletionEvent.getTags().contains(eventTag));

    jpaCacheServiceIF.deleteEvent(secondDeletionEvent);

    List<? extends BaseEvent> allDeletionJpaEventEntities = jpaCacheServiceIF.getAllDeletionEventIds()
        .stream()
        .map(jpaCacheServiceIF::getEventByUid)
        .flatMap(Optional::stream)
        .toList();
    assertEquals(allDeletedEventsSizeAfterFirstDeletion + 1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(event -> log.debug("deletionDbId: {}", event.getId()));

    assertTrue(allDeletionJpaEventEntities.stream().anyMatch(secondBaseEventToDelete::equals));

    List<? extends BaseEvent> allAfterSecondDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

//    assertTrue(allAfterSecondDeletion.stream().noneMatch(secondBaseEventToDelete::equals));
    assertTrue(allAfterSecondDeletion.stream().map(BaseEvent::getId).noneMatch(secondEventToDelete.getId()::equals));
    assertTrue(allAfterSecondDeletion.stream().map(BaseEvent::getId).noneMatch(firstDeletedEventId::equals));
  }
}
