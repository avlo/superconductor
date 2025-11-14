package com.prosilion.superconductor.h2db.service;

import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
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
import com.prosilion.superconductor.lib.jpa.service.JpaCacheServiceIF;
import java.util.ArrayList;
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
  private final GenericEventRecord savedGenericEventRecord;

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
    this.savedGenericEventRecord = jpaCacheServiceIF.save(textNoteEvent);
  }

  @Test
  void testGetByEventId() {
    assertNotNull(savedGenericEventRecord);
    log.info("saved id: {}", savedGenericEventRecord);

    List<GenericEventRecord> all = jpaCacheServiceIF.getAll();

    assertTrue(all.stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(savedGenericEventRecord.getId())));

    EventIF firstRetrievedEventEntityIF = jpaCacheServiceIF.getEventByEventId(savedGenericEventRecord.getId()).orElseThrow();
    assertEquals(savedGenericEventRecord.getId(), firstRetrievedEventEntityIF.getId());

    EventIF secondRetrievedEntityIF = jpaCacheServiceIF.getEventByEventId(savedGenericEventRecord.getId()).orElseThrow();
    assertEquals(savedGenericEventRecord.getId(), secondRetrievedEntityIF.getId());
    assertEquals(savedGenericEventRecord, secondRetrievedEntityIF);

    assertNotNull(savedGenericEventRecord);
    log.info("saved id: {}", savedGenericEventRecord);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(savedGenericEventRecord.getId())));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", savedGenericEventRecord);
    log.info("retrieved ids:");
//    all.stream().map(EventIF::getId).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    EventIF firstRetrieval = jpaCacheServiceIF.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(savedGenericEventRecord.getId(), firstRetrieval.getId());

    EventIF secondRetrieval = jpaCacheServiceIF.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(savedGenericEventRecord.getId(), secondRetrieval.getId());

    assertEquals(firstRetrieval, secondRetrieval);

    int startSize = jpaCacheServiceIF.getAll().size();
    log.debug("startSize: {}", startSize);

    EventIF savedUidOfDuplicate = jpaCacheServiceIF.save(textNoteEvent);
    assertEquals(savedGenericEventRecord, savedUidOfDuplicate);

    int endSize = jpaCacheServiceIF.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);

    log.info("saved id: {}", savedGenericEventRecord);
    String newContent = Factory.lorumIpsum(JpaCacheServiceIT.class);

    all = jpaCacheServiceIF.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    TextNoteEvent eventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    assertEquals(eventToDelete.getId(), jpaCacheServiceIF.save(eventToDelete).getId());

    List<GenericEventRecord> allAfterDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);

    assertTrue(allAfterDeleteMeEvent.stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(eventToDelete.getId())));

    List<Long> allDeletionJpaEventEntitiesBeforeDeletion = jpaCacheServiceIF.getAllDeletionEventIds();

    EventTag eventTag = new EventTag(eventToDelete.getId());

    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(deletionEvent.getTags().contains(eventTag));

    jpaCacheServiceIF.deleteEvent(deletionEvent);

    List<Long> allDeletionJpaEventEntitiesAfterDeletion = jpaCacheServiceIF.getAllDeletionEventIds();
    assertEquals(allDeletionJpaEventEntitiesBeforeDeletion.size() + 1, allDeletionJpaEventEntitiesAfterDeletion.size());

    log.debug(allDeletionJpaEventEntitiesAfterDeletion.toString());
    allDeletionJpaEventEntitiesAfterDeletion.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId));

    List<GenericEventRecord> allAfterDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterDeletion = allAfterDeletion.size();
    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);

    assertTrue(allAfterDeletion.stream().map(EventIF::getId).noneMatch(e -> e.equals(eventToDelete.getId())));
    deleteSecondEvent(sizeAfterDeletion, allDeletionJpaEventEntitiesAfterDeletion.size(), eventToDelete.getId());
  }

  private void deleteSecondEvent(
      int allEventsSizeAfterFirstDeletion,
      int allDeletedEventsSizeAfterFirstDeletion,
      String firstDeletedEventId) {
    String newContent = Factory.lorumIpsum(JpaCacheServiceIT.class);

    List<GenericEventRecord> all = jpaCacheServiceIF.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    TextNoteEvent secondEventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    assertEquals(secondEventToDelete.getId(), jpaCacheServiceIF.save(secondEventToDelete).getId());

    List<GenericEventRecord> allAfterSecondDeleteMeEvent = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);

    assertTrue(jpaCacheServiceIF.getAll().stream()
        .map(EventIF::getId)
        .anyMatch(secondEventToDelete.getId()::equals));

    EventTag eventTag = new EventTag(secondEventToDelete.getId());

    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(secondDeletionEvent.getTags().contains(eventTag));

    jpaCacheServiceIF.deleteEvent(secondDeletionEvent);

    List<Long> allDeletionJpaEventEntities = jpaCacheServiceIF.getAllDeletionEventIds();
    assertEquals(allDeletedEventsSizeAfterFirstDeletion + 1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId));

    List<GenericEventRecord> allAfterSecondDeletion = jpaCacheServiceIF.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

    assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(secondEventToDelete.getId()::equals));
    assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(firstDeletedEventId::equals));
  }
}
