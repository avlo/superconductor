package com.prosilion.superconductor.redis.service;

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
import com.prosilion.superconductor.lib.redis.dto.GenericNosqlEntityKindDto;
import com.prosilion.superconductor.lib.redis.service.RedisCacheServiceIF;
import com.prosilion.superconductor.redis.util.Factory;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
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
@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
public class RedisCacheServiceIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PubKeyTag P_TAG = Factory.createPubKeyTag(IDENTITY);

  private static final EventTag E_TAG = Factory.createEventTag(RedisCacheServiceIT.class);
  private static final GeohashTag G_TAG = Factory.createGeohashTag(RedisCacheServiceIT.class);
  private static final HashtagTag T_TAG = Factory.createHashtagTag(RedisCacheServiceIT.class);
  private static final SubjectTag SUBJECT_TAG = Factory.createSubjectTag(RedisCacheServiceIT.class);
  private static final PriceTag PRICE_TAG = Factory.createPriceTag();

  private final static String CONTENT = Factory.lorumIpsum(RedisCacheServiceIT.class);

  private final RedisCacheServiceIF redisCacheService;
  private final TextNoteEvent textNoteEvent;
  private final List<BaseTag> tags;
  private final BaseEvent eventNosqlBaseEvent;

  @Autowired
  public RedisCacheServiceIT(RedisCacheServiceIF redisCacheServiceIF) {
    this.redisCacheService = redisCacheServiceIF;

    this.tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(PRICE_TAG);

    this.textNoteEvent = new TextNoteEvent(IDENTITY, tags, CONTENT);
    this.eventNosqlBaseEvent = redisCacheServiceIF.save(textNoteEvent);
  }

  @Test
  void testGetByEventId() {
    assertNotNull(eventNosqlBaseEvent);
    log.info("saved id: {}", eventNosqlBaseEvent);

    List<? extends BaseEvent> all = redisCacheService.getAll();

    assertTrue(all.stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(eventNosqlBaseEvent.getId())));

    EventIF firstRetrievedEventEntityIF = redisCacheService.getEventByEventId(eventNosqlBaseEvent.getId()).orElseThrow();
    assertEquals(eventNosqlBaseEvent.getId(), firstRetrievedEventEntityIF.getId());

    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(textNoteEvent);
    EventIF firstEntityIF = firstDto.convertDtoToNosqlEntity();
//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded    
//    assertEquals(firstEntityIF, firstRetrievedEventEntityIF);

    EventIF secondRetrievedEntityIF = redisCacheService.getEventByEventId(eventNosqlBaseEvent.getId()).orElseThrow();
    assertEquals(eventNosqlBaseEvent.getId(), secondRetrievedEntityIF.getId());

    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(textNoteEvent);
    EventIF secondEntityIF = secondDto.convertDtoToNosqlEntity();

//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded
//    assertEquals(secondEntityIF, secondRetrievedEntityIF);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testGetByEventIdString() {
    assertNotNull(eventNosqlBaseEvent);
    log.info("saved id: {}", eventNosqlBaseEvent);

    assertTrue(redisCacheService.getAll().stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(eventNosqlBaseEvent.getId())));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", eventNosqlBaseEvent);
    log.info("retrieved ids:");
//    all.stream().map(EventIF::getId).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    EventIF firstRetrieval = redisCacheService.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(eventNosqlBaseEvent.getId(), firstRetrieval.getId());

    EventIF secondRetrieval = redisCacheService.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(eventNosqlBaseEvent.getId(), secondRetrieval.getId());

    assertEquals(firstRetrieval, secondRetrieval);

    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(textNoteEvent);
    EventIF firstEntity = firstDto.convertDtoToNosqlEntity();
//    assertEquals(firstEntity, firstRetrieval);

    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(textNoteEvent);
    EventIF secondEntity = secondDto.convertDtoToNosqlEntity();

    assertEquals(firstEntity, secondEntity);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = redisCacheService.getAll().size();
    log.debug("startSize: {}", startSize);

    EventIF savedUidOfDuplicate = redisCacheService.save(textNoteEvent);
    assertEquals(eventNosqlBaseEvent, savedUidOfDuplicate);

    int endSize = redisCacheService.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() {
    log.info("saved id: {}", eventNosqlBaseEvent);
    String newContent = Factory.lorumIpsum(RedisCacheServiceIT.class);

    List<? extends BaseEvent> all = redisCacheService.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    TextNoteEvent eventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    assertEquals(eventToDelete.getId(), redisCacheService.save(eventToDelete).getId());

    List<? extends BaseEvent> allAfterDeleteMeEvent = redisCacheService.getAll();
    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);

    assertTrue(allAfterDeleteMeEvent.stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(eventToDelete.getId())));

    List<String> allDeletionJpaEventEntitiesBeforeDeletion = redisCacheService.getAllDeletionEventIds();

    EventTag eventTag = new EventTag(eventToDelete.getId());

    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(deletionEvent.getTags().contains(eventTag));

    redisCacheService.deleteEvent(deletionEvent);

    List<String> allDeletionJpaEventEntitiesAfterDeletion = redisCacheService.getAllDeletionEventIds();
    assertEquals(allDeletionJpaEventEntitiesBeforeDeletion.size() + 1, allDeletionJpaEventEntitiesAfterDeletion.size());

    log.debug(allDeletionJpaEventEntitiesAfterDeletion.toString());
    allDeletionJpaEventEntitiesAfterDeletion.forEach(eventId -> {
      log.debug("deletionDbEventId: {}", eventId);
    });

    assertTrue(allDeletionJpaEventEntitiesAfterDeletion.stream().anyMatch(eventToDelete.getId()::equals));

    List<? extends BaseEvent> allAfterDeletion = redisCacheService.getAll();
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
    String newContent = Factory.lorumIpsum(RedisCacheServiceIT.class);

    List<? extends BaseEvent> all = redisCacheService.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    TextNoteEvent secondEventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
    assertEquals(secondEventToDelete.getId(), redisCacheService.save(secondEventToDelete).getId());

    List<? extends BaseEvent> allAfterSecondDeleteMeEvent = redisCacheService.getAll();
    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);

    assertTrue(redisCacheService.getAll().stream()
        .map(EventIF::getId)
        .anyMatch(secondEventToDelete.getId()::equals));

    EventTag eventTag = new EventTag(secondEventToDelete.getId());

    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(secondDeletionEvent.getTags().contains(eventTag));

    redisCacheService.deleteEvent(secondDeletionEvent);

    List<String> allDeletionJpaEventEntities = redisCacheService.getAllDeletionEventIds();
    assertEquals(allDeletedEventsSizeAfterFirstDeletion + 1, allDeletionJpaEventEntities.size());

    log.debug(allDeletionJpaEventEntities.toString());
    allDeletionJpaEventEntities.forEach(eventId -> {
      log.debug("deletionDbEventId: {}", eventId);
    });

    assertTrue(allDeletionJpaEventEntities.stream().anyMatch(secondEventToDelete.getId()::equals));

    List<? extends BaseEvent> allAfterSecondDeletion = redisCacheService.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

    assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(secondEventToDelete.getId()::equals));
    assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(firstDeletedEventId::equals));
  }
}
