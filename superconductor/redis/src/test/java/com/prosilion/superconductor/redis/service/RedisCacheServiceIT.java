package com.prosilion.superconductor.redis.service;

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
import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.dto.GenericNosqlEntityKindDto;
import com.prosilion.superconductor.lib.redis.service.RedisCacheServiceIF;
import com.prosilion.superconductor.redis.util.Factory;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
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
  private final EventNosqlEntityIF eventNosqlEntityIF;

  @Autowired
  public RedisCacheServiceIT(RedisCacheServiceIF redisCacheServiceIF) throws NoSuchAlgorithmException {
    this.redisCacheService = redisCacheServiceIF;

    this.tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(PRICE_TAG);

    this.textNoteEvent = new TextNoteEvent(IDENTITY, tags, CONTENT);
    this.eventNosqlEntityIF = redisCacheServiceIF.save(textNoteEvent);
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

    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(textNoteEvent);
    EventNosqlEntityIF firstEntityIF = firstDto.convertDtoToNosqlEntity();
//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded    
//    assertEquals(firstEntityIF, firstRetrievedEventEntityIF);

    EventNosqlEntityIF secondRetrievedEntityIF = redisCacheService.getEventByEventId(eventNosqlEntityIF.getEventId()).orElseThrow();
    assertEquals(eventNosqlEntityIF.getEventId(), secondRetrievedEntityIF.getId());

    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(textNoteEvent);
    EventNosqlEntityIF secondEntityIF = secondDto.convertDtoToNosqlEntity();

//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded
//    assertEquals(secondEntityIF, secondRetrievedEntityIF);
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

    EventNosqlEntityIF firstRetrieval = redisCacheService.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(eventNosqlEntityIF.getId(), firstRetrieval.getId());

    EventNosqlEntityIF secondRetrieval = redisCacheService.getEventByEventId(textNoteEvent.getId()).orElseThrow();
    assertEquals(eventNosqlEntityIF.getId(), secondRetrieval.getId());

    assertEquals(firstRetrieval, secondRetrieval);

    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(textNoteEvent);
    EventNosqlEntityIF firstEntity = firstDto.convertDtoToNosqlEntity();
//    assertEquals(firstEntity, firstRetrieval);

    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(textNoteEvent);
    EventNosqlEntityIF secondEntity = secondDto.convertDtoToNosqlEntity();

    assertEquals(firstEntity, secondEntity);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = redisCacheService.getAll().size();
    log.debug("startSize: {}", startSize);

    EventNosqlEntityIF savedUidOfDuplicate = redisCacheService.save(textNoteEvent);
    assertEquals(eventNosqlEntityIF, savedUidOfDuplicate);

    int endSize = redisCacheService.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() throws NoSuchAlgorithmException {
    log.info("saved id: {}", eventNosqlEntityIF);
    String newContent = Factory.lorumIpsum(RedisCacheServiceIT.class);

    List<EventNosqlEntityIF> all = redisCacheService.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    TextNoteEvent eventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
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
      String firstDeletedEventId) throws NoSuchAlgorithmException {
    String newContent = Factory.lorumIpsum(RedisCacheServiceIT.class);

    List<EventNosqlEntityIF> all = redisCacheService.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    TextNoteEvent secondEventToDelete = new TextNoteEvent(IDENTITY, tags, newContent);
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
