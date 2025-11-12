package com.prosilion.superconductor.redis.service;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
import com.prosilion.superconductor.redis.util.Factory;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService.NON_EXISTENT_EVENT_ID_S;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RedisCacheServiceGenericEventRecordUsingFormulaEventIT {
  private static final Relay relay = new Relay("ws://localhost:5555");
  private static final Identity IDENTITY = Factory.createNewIdentity();

  private final CacheFormulaEventService cacheFormulaEventService;
  private final BadgeDefinitionAwardEvent badgeDefinitionAwardEvent;
  private final FormulaEvent formulaEvent;

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag("UNIT_UPVOTE");
  public final String PLUS_ONE_FORMULA = "+1";

  private FormulaEvent savedFormulaEvent;

  @Autowired
  public RedisCacheServiceGenericEventRecordUsingFormulaEventIT(CacheFormulaEventService cacheFormulaEventService) throws ParseException {
    this.cacheFormulaEventService = cacheFormulaEventService;
    this.badgeDefinitionAwardEvent = new BadgeDefinitionAwardEvent(IDENTITY, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);

    this.formulaEvent =
        new FormulaEvent(
            IDENTITY,
            badgeDefinitionAwardEvent,
            PLUS_ONE_FORMULA);
  }

  @Test
  @Order(1)
  void testNonExistentEventTag() {
    assertTrue(
        assertThrows(NostrException.class, () ->
            cacheFormulaEventService.save(this.formulaEvent))
            .getMessage().contains(
                Strings.concat( 
                    String.format(NON_EXISTENT_EVENT_ID_S, formulaEvent.getId()),
                    String.format("[%s]", badgeDefinitionAwardEvent.getId()))));
  }

  @Test
  @Order(2)
  void testExistentEventTag() {
    cacheFormulaEventService.save(this.badgeDefinitionAwardEvent);
    this.savedFormulaEvent = cacheFormulaEventService.save(this.formulaEvent);
  }

  @Test
  @Order(3)
  void testGetByEventId() {
    assertNotNull(savedFormulaEvent);
    log.info("saved id: {}", savedFormulaEvent);

    List<FormulaEvent> all = cacheFormulaEventService.getAll();

    assertTrue(all.stream()
        .map(FormulaEvent::getId)
        .anyMatch(e -> e.equals(savedFormulaEvent.getId())));

    FormulaEvent firstRetrievedEventEntityIF = cacheFormulaEventService.getEventByEventId(savedFormulaEvent.getId()).orElseThrow();
    assertEquals(savedFormulaEvent.getId(), firstRetrievedEventEntityIF.getId());
    assertEquals(savedFormulaEvent, firstRetrievedEventEntityIF);

    FormulaEvent secondRetrievedEntityIF = cacheFormulaEventService.getEventByEventId(savedFormulaEvent.getId()).orElseThrow();
    assertEquals(savedFormulaEvent.getId(), secondRetrievedEntityIF.getId());
    assertEquals(savedFormulaEvent, secondRetrievedEntityIF);
    assertEquals(firstRetrievedEventEntityIF, secondRetrievedEntityIF);
  }

  @Test
  void testGetByEventIdString() {
    assertNotNull(savedFormulaEvent);
    log.info("saved id: {}", savedFormulaEvent);

    assertTrue(cacheFormulaEventService.getAll().stream()
        .map(FormulaEvent::getId)
        .anyMatch(e -> e.equals(savedFormulaEvent.getId())));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", savedFormulaEvent);
    log.info("retrieved ids:");
//    all.stream().map(FormulaEvent::getId).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    FormulaEvent firstRetrieval = cacheFormulaEventService.getEventByEventId(savedFormulaEvent.getId()).orElseThrow();
    assertEquals(savedFormulaEvent.getId(), firstRetrieval.getId());

    FormulaEvent secondRetrieval = cacheFormulaEventService.getEventByEventId(savedFormulaEvent.getId()).orElseThrow();
    assertEquals(savedFormulaEvent.getId(), secondRetrieval.getId());
    assertEquals(firstRetrieval, secondRetrieval);
  }

  @Test
  void testDuplicateSaveAttempt() {
    int startSize = cacheFormulaEventService.getAll().size();
    log.debug("startSize: {}", startSize);

    FormulaEvent savedUidOfDuplicate = cacheFormulaEventService.save(savedFormulaEvent);
    assertEquals(savedFormulaEvent, savedUidOfDuplicate);

    int endSize = cacheFormulaEventService.getAll().size();
    log.debug("endSize: {}", endSize);
    assertEquals(startSize, endSize);
  }

  @Test
  void testDeletedEvent() throws ParseException {
    log.info("saved id: {}", savedFormulaEvent);

    List<FormulaEvent> all = cacheFormulaEventService.getAll();
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
    assertEquals(eventToDelete.getId(), cacheFormulaEventService.save(eventToDelete).getId());

    List<FormulaEvent> allAfterDeleteMeEvent = cacheFormulaEventService.getAll();
    int sizeAfterDeleteMeEvent = allAfterDeleteMeEvent.size();
    log.debug("sizeAfterDeleteMeEvent: {}", sizeAfterDeleteMeEvent);
    assertEquals(sizeBeforeDeleteMeEvent + 1, sizeAfterDeleteMeEvent);

    assertTrue(allAfterDeleteMeEvent.stream()
        .anyMatch(e -> e.equals(eventToDelete.getGenericEventRecord())));

    assertTrue(allAfterDeleteMeEvent.stream()
        .map(FormulaEvent::getId)
        .anyMatch(e -> e.equals(eventToDelete.getId())));

    EventTag eventTag = new EventTag(eventToDelete.getId());

    DeletionEvent deletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(deletionEvent.getTags().contains(eventTag));

    cacheFormulaEventService.deleteEvent(deletionEvent);

    List<FormulaEvent> allAfterDeletion = cacheFormulaEventService.getAll();
    int sizeAfterDeletion = allAfterDeletion.size();
    log.debug("sizeAfterDeletion: {}", sizeAfterDeletion);
    assertEquals(sizeAfterDeleteMeEvent - 1, sizeAfterDeletion);

    assertTrue(allAfterDeletion.stream().map(FormulaEvent::getId).noneMatch(e -> e.equals(eventToDelete.getId())));
  }

  private void deleteSecondEvent(
      int allEventsSizeAfterFirstDeletion,
      int allDeletedEventsSizeAfterFirstDeletion,
      String firstDeletedEventId) throws ParseException {

    List<FormulaEvent> all = cacheFormulaEventService.getAll();
    int sizeBeforeSecondDeleteMeEvent = all.size();
    log.debug("sizeBeforeSecondDeleteMeEvent: {}", sizeBeforeSecondDeleteMeEvent);
    assertEquals(allEventsSizeAfterFirstDeletion, sizeBeforeSecondDeleteMeEvent);

    FormulaEvent secondEventToDelete =
        new FormulaEvent(IDENTITY, new BadgeDefinitionAwardEvent(
            IDENTITY,
            upvoteIdentifierTag,
            relay,
            PLUS_ONE_FORMULA), PLUS_ONE_FORMULA);
    FormulaEvent secondBaseEventToDelete = cacheFormulaEventService.save(secondEventToDelete);

    List<FormulaEvent> allAfterSecondDeleteMeEvent = cacheFormulaEventService.getAll();
    int sizeAfterSecondDeleteMeEvent = allAfterSecondDeleteMeEvent.size();
    log.debug("sizeAfterSecondDeleteMeEvent: {}", sizeAfterSecondDeleteMeEvent);
    assertEquals(sizeBeforeSecondDeleteMeEvent + 1, sizeAfterSecondDeleteMeEvent);

    assertTrue(cacheFormulaEventService.getAll().stream()
        .anyMatch(secondBaseEventToDelete::equals));

    EventTag eventTag = new EventTag(secondEventToDelete.getId());

    DeletionEvent secondDeletionEvent = new DeletionEvent(IDENTITY, List.of(eventTag), Factory.lorumIpsum());
    assertTrue(secondDeletionEvent.getTags().contains(eventTag));

    cacheFormulaEventService.deleteEvent(secondDeletionEvent);

    List<FormulaEvent> allAfterSecondDeletion = cacheFormulaEventService.getAll();
    int sizeAfterSecondDeletion = allAfterSecondDeletion.size();
    log.debug("sizeAfterSecondDeletion: {}", sizeAfterSecondDeletion);
    assertEquals(sizeAfterSecondDeleteMeEvent - 1, sizeAfterSecondDeletion);

//    assertTrue(allAfterSecondDeletion.stream().noneMatch(secondBaseEventToDelete::equals));
    assertTrue(allAfterSecondDeletion.stream().map(FormulaEvent::getId).noneMatch(secondEventToDelete.getId()::equals));
    assertTrue(allAfterSecondDeletion.stream().map(FormulaEvent::getId).noneMatch(firstDeletedEventId::equals));
  }
}
