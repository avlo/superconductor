package com.prosilion.superconductor.h2db.service;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
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
import com.prosilion.superconductor.lib.jpa.service.JpaCacheServiceIF;
import java.util.ArrayList;
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
public class JpaCacheServiceIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PubKeyTag P_TAG = Factory.createPubKeyTag(IDENTITY);

  private static final EventTag E_TAG = Factory.createEventTag(JpaCacheServiceIT.class);
  private static final GeohashTag G_TAG = Factory.createGeohashTag(JpaCacheServiceIT.class);
  private static final HashtagTag T_TAG = Factory.createHashtagTag(JpaCacheServiceIT.class);
  private static final SubjectTag SUBJECT_TAG = Factory.createSubjectTag(JpaCacheServiceIT.class);
  private static final PriceTag PRICE_TAG = Factory.createPriceTag();

  private static final String CONTENT = Factory.lorumIpsum(JpaCacheServiceIT.class);

  private final JpaCacheServiceIF jpaCacheServiceIF;
  private final TextNoteEvent textNoteEvent;
  private final List<BaseTag> tags;
  private final BaseEvent savedBaseEvent;

  @Autowired
  public JpaCacheServiceIT(JpaCacheServiceIF jpaCacheServiceIF) {
    this.jpaCacheServiceIF = jpaCacheServiceIF;

    this.tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(PRICE_TAG);

    this.textNoteEvent = new TextNoteEvent(IDENTITY, tags, CONTENT);
    this.savedBaseEvent = jpaCacheServiceIF.save(textNoteEvent);
  }

  @Test
  void testGetByEventId() {
    assertNotNull(savedBaseEvent);
    log.info("saved id: {}", savedBaseEvent);

    List<? extends BaseEvent> all = jpaCacheServiceIF.getAll();

    assertTrue(all.stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(savedBaseEvent.getId())));

    BaseEvent firstRetrievedEventEntityIF = jpaCacheServiceIF.getEvent(savedBaseEvent).orElseThrow();
    assertEquals(savedBaseEvent, firstRetrievedEventEntityIF);

    GenericEventKindDto firstDto = new GenericEventKindDto(savedBaseEvent);
    BaseEvent secondRetrievedEntityIF = jpaCacheServiceIF.getEventByEventId(savedBaseEvent.getId()).orElseThrow();
    assertEquals(firstRetrievedEventEntityIF, secondRetrievedEntityIF);

    GenericEventKindDto secondDto = new GenericEventKindDto(textNoteEvent);
    BaseEvent secondEntityIF = secondDto.baseEvent();

//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded
    assertEquals(secondEntityIF, secondRetrievedEntityIF);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testGetByEventIdString() {
    assertNotNull(savedBaseEvent);
    log.info("saved id: {}", savedBaseEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(savedBaseEvent.getId())));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", savedBaseEvent);
    log.info("retrieved ids:");
//    all.stream().map(EventEntityIF::getUid).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    BaseEvent firstRetrieval = jpaCacheServiceIF.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(savedBaseEvent, firstRetrieval);

    BaseEvent secondRetrieval = jpaCacheServiceIF.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(savedBaseEvent, secondRetrieval);

    assertEquals(firstRetrieval, secondRetrieval);

    GenericEventKindDto firstDto = new GenericEventKindDto(textNoteEvent);
    GenericEventKindDto secondDto = new GenericEventKindDto(textNoteEvent);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = jpaCacheServiceIF.getAll().size();
    log.debug("startSize: {}", startSize);

    BaseEvent savedUidOfDuplicate = jpaCacheServiceIF.save(textNoteEvent);
    assertEquals(savedBaseEvent, savedUidOfDuplicate);

    int endSize = jpaCacheServiceIF.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() {
    log.info("saved id: {}", savedBaseEvent);
    String newContent = Factory.lorumIpsum(JpaCacheServiceIT.class);

    List<? extends BaseEvent> all = jpaCacheServiceIF.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    TextNoteEvent textNoteEventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    BaseEvent eventToDelete = jpaCacheServiceIF.save(textNoteEventToDelete);

    List<? extends BaseEvent> allAfterDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .anyMatch(eventToDelete::equals));

    EventTag eventTag = new EventTag(textNoteEventToDelete.getId());

    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(deletionEvent.getTags().contains(eventTag));

    jpaCacheServiceIF.deleteEvent(deletionEvent);

    List<? extends BaseEvent> allDeletionJpaEventEntities = jpaCacheServiceIF.getAllDeletionEventIds()
        .stream()
        .map(jpaCacheServiceIF::getEventByUid)
        .flatMap(Optional::stream)
        .toList();
    assertEquals(1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId.toString()));

    assertTrue(allDeletionJpaEventEntities.stream().anyMatch(eventToDelete::equals));

    List<? extends BaseEvent> allAfterDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterDeletion = allAfterDeletion.size();
    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);

    assertTrue(allAfterDeletion.stream().noneMatch(eventToDelete::equals));
    assertTrue(allAfterDeletion.stream().noneMatch(textNoteEventToDelete::equals));

    deleteSecondEvent(sizeAfterDeletion, allDeletionJpaEventEntities.size(), textNoteEventToDelete.getId());
  }

  private void deleteSecondEvent(
      int allEventsSizeAfterFirstDeletion,
      int allDeletedEventsSizeAfterFirstDeletion,
      String firstDeletedEventId) {
    String newContent = Factory.lorumIpsum(JpaCacheServiceIT.class);

    List<? extends BaseEvent> all = jpaCacheServiceIF.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    TextNoteEvent secondEventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    BaseEvent secondEventToDeleteUid = jpaCacheServiceIF.save(secondEventToDelete);

    List<? extends BaseEvent> allAfterSecondDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .anyMatch(secondEventToDeleteUid::equals));

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
    allDeletionJpaEventEntities.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId.toString()));

    assertTrue(allDeletionJpaEventEntities.stream().anyMatch(secondEventToDeleteUid::equals));

    List<? extends BaseEvent> allAfterSecondDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

    assertTrue(allAfterSecondDeletion.stream().noneMatch(secondEventToDeleteUid::equals));
    assertTrue(allAfterSecondDeletion.stream().map(BaseEvent::getId).noneMatch(secondEventToDelete.getId()::equals));
    assertTrue(allAfterSecondDeletion.stream().map(BaseEvent::getId).noneMatch(firstDeletedEventId::equals));
  }
}
