package com.prosilion.superconductor.service;

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
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.util.Factory;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class BaseCacheServiceIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PubKeyTag P_TAG = Factory.createPubKeyTag(IDENTITY);

  private static final EventTag E_TAG = Factory.createEventTag(BaseCacheServiceIT.class);
  private static final GeohashTag G_TAG = Factory.createGeohashTag(BaseCacheServiceIT.class);
  private static final HashtagTag T_TAG = Factory.createHashtagTag(BaseCacheServiceIT.class);
  private static final SubjectTag SUBJECT_TAG = Factory.createSubjectTag(BaseCacheServiceIT.class);
  private static final PriceTag PRICE_TAG = Factory.createPriceTag();

  private static final String CONTENT = Factory.lorumIpsum(BaseCacheServiceIT.class);

  private final CacheServiceIF cacheServiceIF;
  private final TextNoteEvent textNoteEvent;
  private final List<BaseTag> tags;
  private final GenericEventRecord savedGenericEventRecord;

  //  @Autowired
  public BaseCacheServiceIT(CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;

    this.tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(PRICE_TAG);

    this.textNoteEvent = new TextNoteEvent(IDENTITY, tags, CONTENT);
    this.savedGenericEventRecord = cacheServiceIF.save(textNoteEvent);
  }

  @Test
  void testGetByEventId() {
    Assertions.assertNotNull(savedGenericEventRecord);
    log.info("saved id: {}", savedGenericEventRecord);

    List<GenericEventRecord> all = cacheServiceIF.getAll();

    Assertions.assertTrue(all.stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(savedGenericEventRecord.getId())));

    EventIF firstRetrievedEventEntityIF = cacheServiceIF.getEventByEventId(savedGenericEventRecord.getId()).orElseThrow();
    Assertions.assertEquals(savedGenericEventRecord.getId(), firstRetrievedEventEntityIF.getId());

//    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(textNoteEvent);
//    EventIF firstEntityIF = firstDto.convertDtoToNosqlEntity();
//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded    
//    Assertions.assertEquals(firstEntityIF, firstRetrievedEventEntityIF);

    EventIF secondRetrievedEntityIF = cacheServiceIF.getEventByEventId(savedGenericEventRecord.getId()).orElseThrow();
    Assertions.assertEquals(savedGenericEventRecord.getId(), secondRetrievedEntityIF.getId());

//    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(textNoteEvent);
//    EventIF secondEntityIF = secondDto.convertDtoToNosqlEntity();

//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded
//    Assertions.assertEquals(secondEntityIF, secondRetrievedEntityIF);
//    Assertions.assertEquals(firstDto, secondDto);
  }

  @Test
  void testGetByEventIdString() {
    Assertions.assertNotNull(savedGenericEventRecord);
    log.info("saved id: {}", savedGenericEventRecord);

    Assertions.assertTrue(cacheServiceIF.getAll().stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(savedGenericEventRecord.getId())));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", savedGenericEventRecord);
    log.info("retrieved ids:");
//    all.stream().map(EventIF::getId).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    EventIF firstRetrieval = cacheServiceIF.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    Assertions.assertEquals(savedGenericEventRecord.getId(), firstRetrieval.getId());

    EventIF secondRetrieval = cacheServiceIF.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    Assertions.assertEquals(savedGenericEventRecord.getId(), secondRetrieval.getId());

    Assertions.assertEquals(firstRetrieval, secondRetrieval);

//    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(textNoteEvent);
//    EventIF firstEntity = firstDto.convertDtoToNosqlEntity();
//    Assertions.assertEquals(firstEntity, firstRetrieval);

//    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(textNoteEvent);
//    EventIF secondEntity = secondDto.convertDtoToNosqlEntity();

//    Assertions.assertEquals(firstEntity, secondEntity);
//    Assertions.assertEquals(firstDto, secondDto);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = cacheServiceIF.getAll().size();
    log.debug("startSize: {}", startSize);

    EventIF savedUidOfDuplicate = cacheServiceIF.save(textNoteEvent);
    Assertions.assertEquals(savedGenericEventRecord, savedUidOfDuplicate);

    int endSize = cacheServiceIF.getAll().size();
    log.debug("endSize: {}", endSize);
    Assertions.assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() {
    log.info("saved id: {}", savedGenericEventRecord);
    String newContent = Factory.lorumIpsum(BaseCacheServiceIT.class);

    List<GenericEventRecord> all = cacheServiceIF.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    TextNoteEvent eventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    Assertions.assertEquals(eventToDelete.getId(), cacheServiceIF.save(eventToDelete).getId());

    List<GenericEventRecord> allAfterDeleteMeEvent = cacheServiceIF.getAll();
    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
    Assertions.assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);

    Assertions.assertTrue(allAfterDeleteMeEvent.stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(eventToDelete.getId())));

    List<String> allDeletionJpaEventEntitiesBeforeDeletion = cacheServiceIF.getAllDeletionEventIds();

    EventTag eventTag = new EventTag(eventToDelete.getId());

    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    Assertions.assertTrue(deletionEvent.getTags().contains(eventTag));

    cacheServiceIF.deleteEvent(deletionEvent);

    List<String> allDeletionJpaEventEntitiesAfterDeletion = cacheServiceIF.getAllDeletionEventIds();
    Assertions.assertEquals(allDeletionJpaEventEntitiesBeforeDeletion.size() + 1, allDeletionJpaEventEntitiesAfterDeletion.size());

    log.debug(allDeletionJpaEventEntitiesAfterDeletion.toString());
    allDeletionJpaEventEntitiesAfterDeletion.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId));

    Assertions.assertTrue(allDeletionJpaEventEntitiesAfterDeletion.stream().anyMatch(eventToDelete.getId()::equals));

    List<GenericEventRecord> allAfterDeletion = cacheServiceIF.getAll();
    int sizeAfterDeletion = allAfterDeletion.size();
    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
    Assertions.assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);

    Assertions.assertTrue(allAfterDeletion.stream().map(EventIF::getId).noneMatch(e -> e.equals(eventToDelete.getId())));
    deleteSecondEvent(sizeAfterDeletion, allDeletionJpaEventEntitiesAfterDeletion.size(), eventToDelete.getId());
  }

  private void deleteSecondEvent(
      int allEventsSizeAfterFirstDeletion,
      int allDeletedEventsSizeAfterFirstDeletion,
      String firstDeletedEventId) {
    String newContent = Factory.lorumIpsum(BaseCacheServiceIT.class);

    List<GenericEventRecord> all = cacheServiceIF.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    Assertions.assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    TextNoteEvent secondEventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    Assertions.assertEquals(secondEventToDelete.getId(), cacheServiceIF.save(secondEventToDelete).getId());

    List<GenericEventRecord> allAfterSecondDeleteMeEvent = cacheServiceIF.getAll();
    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
    Assertions.assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);

    Assertions.assertTrue(cacheServiceIF.getAll().stream()
        .map(EventIF::getId)
        .anyMatch(secondEventToDelete.getId()::equals));

    EventTag eventTag = new EventTag(secondEventToDelete.getId());

    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    Assertions.assertTrue(secondDeletionEvent.getTags().contains(eventTag));

    cacheServiceIF.deleteEvent(secondDeletionEvent);

    List<String> allDeletionJpaEventEntities = cacheServiceIF.getAllDeletionEventIds();
    Assertions.assertEquals(allDeletedEventsSizeAfterFirstDeletion + 1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId));

    Assertions.assertTrue(allDeletionJpaEventEntities.stream().anyMatch(secondEventToDelete.getId()::equals));

    List<GenericEventRecord> allAfterSecondDeletion = cacheServiceIF.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    Assertions.assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

    Assertions.assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(secondEventToDelete.getId()::equals));
    Assertions.assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(firstDeletedEventId::equals));
  }
}
