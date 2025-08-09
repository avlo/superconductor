package com.prosilion.superconductor.h2db.service;

import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.GeohashTag;
import com.prosilion.nostr.tag.HashtagTag;
import com.prosilion.nostr.tag.PriceTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.SubjectTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.h2db.util.Factory;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import com.prosilion.superconductor.lib.jpa.service.JpaCacheServiceIF;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
@SpringBootTest
@ActiveProfiles("test")
public class JpaCacheServiceIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PubKeyTag P_TAG = Factory.createPubKeyTag(IDENTITY);

  private static final EventTag E_TAG = Factory.createEventTag(JpaCacheServiceIT.class);
  private static final GeohashTag G_TAG = Factory.createGeohashTag(JpaCacheServiceIT.class);
  private static final HashtagTag T_TAG = Factory.createHashtagTag(JpaCacheServiceIT.class);
  private static final SubjectTag SUBJECT_TAG = Factory.createSubjectTag(JpaCacheServiceIT.class);
  private static final PriceTag PRICE_TAG = Factory.createPriceTag();

  private final static String CONTENT = Factory.lorumIpsum(JpaCacheServiceIT.class);

  private final JpaCacheServiceIF jpaCacheServiceIF;
  private final TextNoteEvent textNoteEvent;
  private final List<BaseTag> tags;
  private final Long savedId;

  @Autowired
  public JpaCacheServiceIT(JpaCacheServiceIF jpaCacheServiceIF) throws NoSuchAlgorithmException {
    this.jpaCacheServiceIF = jpaCacheServiceIF;

    this.tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(PRICE_TAG);

    this.textNoteEvent = new TextNoteEvent(IDENTITY, tags, CONTENT);
    this.savedId = jpaCacheServiceIF.save(textNoteEvent);
  }

  @Test
  void testGetByEventId() {
    assertNotNull(savedId);
    log.info("saved id: {}", savedId);

    List<EventEntityIF> all = jpaCacheServiceIF.getAll();

    assertTrue(all.stream()
        .map(EventEntityIF::getUid)
        .anyMatch(savedId::equals));

    EventEntityIF firstRetrievedEventEntityIF = jpaCacheServiceIF.getEventByUid(savedId).orElseThrow();
    assertEquals(savedId, firstRetrievedEventEntityIF.getUid());

    GenericEventKindDto firstDto = new GenericEventKindDto(textNoteEvent);
    EventEntityIF firstEntityIF = firstDto.convertDtoToEntity();
//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded    
//    assertEquals(firstEntityIF, firstRetrievedEventEntityIF);

    EventEntityIF secondRetrievedEntityIF = jpaCacheServiceIF.getEventByUid(savedId).orElseThrow();
    assertEquals(savedId, secondRetrievedEntityIF.getUid());

    GenericEventKindDto secondDto = new GenericEventKindDto(textNoteEvent);
    EventEntityIF secondEntityIF = secondDto.convertDtoToEntity();

//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded
//    assertEquals(secondEntityIF, secondRetrievedEntityIF);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testGetByEventIdString() {
    assertNotNull(savedId);
    log.info("saved id: {}", savedId);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .map(EventEntityIF::getUid)
        .anyMatch(savedId::equals));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", savedId);
    log.info("retrieved ids:");
//    all.stream().map(EventEntityIF::getUid).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    EventEntityIF firstRetrieval = jpaCacheServiceIF.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(savedId, firstRetrieval.getUid());

    EventEntityIF secondRetrieval = jpaCacheServiceIF.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(savedId, secondRetrieval.getUid());

    assertEquals(firstRetrieval, secondRetrieval);

    GenericEventKindDto firstDto = new GenericEventKindDto(textNoteEvent);
    EventEntityIF firstEntity = firstDto.convertDtoToEntity();
//    assertEquals(firstEntity, firstRetrieval);

    GenericEventKindDto secondDto = new GenericEventKindDto(textNoteEvent);
    EventEntityIF secondEntity = secondDto.convertDtoToEntity();

    assertEquals(firstEntity, secondEntity);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = jpaCacheServiceIF.getAll().size();
    log.debug("startSize: {}", startSize);

    Long savedUidOfDuplicate = jpaCacheServiceIF.save(textNoteEvent);
    assertEquals(savedId, savedUidOfDuplicate);

    int endSize = jpaCacheServiceIF.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() throws NoSuchAlgorithmException {
    log.info("saved id: {}", savedId);
    String newContent = Factory.lorumIpsum(JpaCacheServiceIT.class);

    List<EventEntityIF> all = jpaCacheServiceIF.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    TextNoteEvent eventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    Long eventToDeleteUid = jpaCacheServiceIF.save(eventToDelete);

    List<EventEntityIF> allAfterDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .map(EventEntityIF::getUid)
        .anyMatch(eventToDeleteUid::equals));

    EventTag eventTag = new EventTag(eventToDelete.getId());

    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(deletionEvent.getTags().contains(eventTag));

    jpaCacheServiceIF.deleteEventEntity(deletionEvent);

    List<DeletionEventEntityIF> allDeletionJpaEventEntities = jpaCacheServiceIF.getAllDeletionEvents();
    assertEquals(1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(event -> {
      log.debug("deletionDbId: {}", event.getId().toString());
      log.debug("deletionDbEventId: {}", event.getEventId().toString());
    });

    assertTrue(allDeletionJpaEventEntities.stream().map(DeletionEventEntityIF::getEventId).anyMatch(eventToDeleteUid::equals));

    List<EventEntityIF> allAfterDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterDeletion = allAfterDeletion.size();
    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);

    assertTrue(allAfterDeletion.stream().map(EventEntityIF::getUid).noneMatch(eventToDeleteUid::equals));
    assertTrue(allAfterDeletion.stream().map(EventEntityIF::getId).noneMatch(eventToDelete.getId()::equals));

    deleteSecondEvent(sizeAfterDeletion, allDeletionJpaEventEntities.size(), eventToDelete.getId());
  }

  private void deleteSecondEvent(
      int allEventsSizeAfterFirstDeletion,
      int allDeletedEventsSizeAfterFirstDeletion,
      String firstDeletedEventId) throws NoSuchAlgorithmException {
    String newContent = Factory.lorumIpsum(JpaCacheServiceIT.class);

    List<EventEntityIF> all = jpaCacheServiceIF.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    TextNoteEvent secondEventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    Long secondEventToDeleteUid = jpaCacheServiceIF.save(secondEventToDelete);

    List<EventEntityIF> allAfterSecondDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .map(EventEntityIF::getUid)
        .anyMatch(secondEventToDeleteUid::equals));

    EventTag eventTag = new EventTag(secondEventToDelete.getId());

    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(secondDeletionEvent.getTags().contains(eventTag));

    jpaCacheServiceIF.deleteEventEntity(secondDeletionEvent);

    List<DeletionEventEntityIF> allDeletionJpaEventEntities = jpaCacheServiceIF.getAllDeletionEvents();
    assertEquals(allDeletedEventsSizeAfterFirstDeletion + 1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(event -> {
      log.debug("deletionDbId: {}", event.getId().toString());
      log.debug("deletionDbEventId: {}", event.getEventId().toString());
    });

    assertTrue(allDeletionJpaEventEntities.stream().map(DeletionEventEntityIF::getEventId).anyMatch(secondEventToDeleteUid::equals));

    List<EventEntityIF> allAfterSecondDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

    assertTrue(allAfterSecondDeletion.stream().map(EventEntityIF::getUid).noneMatch(secondEventToDeleteUid::equals));
    assertTrue(allAfterSecondDeletion.stream().map(EventEntityIF::getId).noneMatch(secondEventToDelete.getId()::equals));
    assertTrue(allAfterSecondDeletion.stream().map(EventEntityIF::getId).noneMatch(firstDeletedEventId::equals));
  }
}
