package com.prosilion.superconductor.redis.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.lib.redis.dto.GenericNosqlEntityKindDto;
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
public class RedisCacheServiceBadgeDefinitionAwardEventIT {
  public static final Relay relay = new Relay("ws://localhost:5555");
  private static final Identity IDENTITY = Factory.createNewIdentity();

  private final RedisCacheServiceIF redisCacheService;
  private final BadgeDefinitionAwardEvent badgeDefinitionAwardEvent;
  private final BaseEvent eventNosqlEntityIFBadgeDefinitionAwardEvent;

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
  public final String PLUS_ONE_FORMULA = "+1";

  @Autowired
  public RedisCacheServiceBadgeDefinitionAwardEventIT(RedisCacheServiceIF redisCacheServiceIF) {
    this.redisCacheService = redisCacheServiceIF;
    this.badgeDefinitionAwardEvent = new BadgeDefinitionAwardEvent(IDENTITY, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
    this.eventNosqlEntityIFBadgeDefinitionAwardEvent = redisCacheServiceIF.save(this.badgeDefinitionAwardEvent);
  }

  @Test
  void testGetByEventId() {
    assertNotNull(eventNosqlEntityIFBadgeDefinitionAwardEvent);
    log.info("saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);

    List<? extends BaseEvent> all = redisCacheService.getAll();

    assertTrue(all.stream()
        .map(BaseEvent::getId)
        .anyMatch(e -> e.equals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId())));

    EventIF firstRetrievedEventEntityIF = redisCacheService.getEventByEventId(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId()).orElseThrow();
    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), firstRetrievedEventEntityIF.getId());

    assertEquals(badgeDefinitionAwardEvent, firstRetrievedEventEntityIF);

    EventIF secondRetrievedEntityIF = redisCacheService.getEventByEventId(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId()).orElseThrow();
    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), secondRetrievedEntityIF.getId());

//    TODO: readd below test after GenericEventKindDto/GenericEventKindType have been upgraded
    assertEquals(firstRetrievedEventEntityIF, secondRetrievedEntityIF);
  }

  @Test
  void testGetByEventIdString() {
    assertNotNull(eventNosqlEntityIFBadgeDefinitionAwardEvent);
    log.info("saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);

    assertTrue(redisCacheService.getAll().stream()
        .map(EventIF::getId)
        .anyMatch(e -> e.equals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId())));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);
    log.info("retrieved ids:");
//    all.stream().map(EventNosqlEntityIF::getId).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    EventIF firstRetrieval = redisCacheService.getEventByEventId(badgeDefinitionAwardEvent.getId()).orElseThrow();
    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), firstRetrieval.getId());

    EventIF secondRetrieval = redisCacheService.getEventByEventId(badgeDefinitionAwardEvent.getId()).orElseThrow();
    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent.getId(), secondRetrieval.getId());

    assertEquals(firstRetrieval, secondRetrieval);

    GenericNosqlEntityKindDto firstDto = new GenericNosqlEntityKindDto(badgeDefinitionAwardEvent);
    EventIF firstEntity = firstDto.convertDtoToNosqlEntity();
//    assertEquals(firstEntity, firstRetrieval);

    GenericNosqlEntityKindDto secondDto = new GenericNosqlEntityKindDto(badgeDefinitionAwardEvent);
    EventIF secondEntity = secondDto.convertDtoToNosqlEntity();

    assertEquals(firstEntity, secondEntity);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = redisCacheService.getAll().size();
    log.debug("startSize: {}", startSize);

    EventIF savedUidOfDuplicate = redisCacheService.save(badgeDefinitionAwardEvent);
    assertEquals(eventNosqlEntityIFBadgeDefinitionAwardEvent, savedUidOfDuplicate);

    int endSize = redisCacheService.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() throws ParseException {
    log.info("saved id: {}", eventNosqlEntityIFBadgeDefinitionAwardEvent);

    List<? extends BaseEvent> all = redisCacheService.getAll();
    int sizeBeforeDeleteMeEvent = all.size();
    log.debug("sizeBeforeDeleteMeEvent: {}", sizeBeforeDeleteMeEvent);

    FormulaEvent eventToDelete = new FormulaEvent(
        IDENTITY,
        new BadgeDefinitionAwardEvent(
            IDENTITY,
            upvoteIdentifierTag,
            relay,
            PLUS_ONE_FORMULA),
        PLUS_ONE_FORMULA);
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
    allDeletionJpaEventEntitiesAfterDeletion.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId));

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
      String firstDeletedEventId) throws ParseException {

    List<? extends BaseEvent> all = redisCacheService.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    FormulaEvent secondEventToDelete =
        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
            IDENTITY, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
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
    allDeletionJpaEventEntities.forEach(eventId -> log.debug("deletionDbEventId: {}", eventId));

    assertTrue(allDeletionJpaEventEntities.stream().anyMatch(secondEventToDelete.getId()::equals));

    List<? extends BaseEvent> allAfterSecondDeletion = redisCacheService.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

    assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(secondEventToDelete.getId()::equals));
    assertTrue(allAfterSecondDeletion.stream().map(EventIF::getId).noneMatch(firstDeletedEventId::equals));
  }
}
