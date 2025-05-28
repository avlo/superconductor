package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.repository.EventEntityRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(
    showSql = false  // true by default
    , includeFilters = @ComponentScan.Filter( // quicker tests, allegedly
    type = FilterType.ASSIGNABLE_TYPE,
    classes = EventEntityRepository.class)
)
@Sql(scripts = {"/bulkevent.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS) // class level @Sql
@ActiveProfiles("test")
//@Sql(scripts = {"/cleanup_event.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class BulkEventEntityRepositoryIT {
  public static final String SIGNATURE = "86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546";
  public static final String EVENT_ID = "dddeee6101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cd";
  public static final String PUB_KEY = "dddeeef81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76985";
  public static final String CONTENT = "abcdefgfedcba";
  public static final Integer KIND = 1;
  public static final Integer NIP = 1;
  public static final long CREATED_AT = 1717357053050L;

  @Autowired
  EventEntityRepository eventEntityRepository;

  @Test
  void testBulkEventEntityRepositoryIdEquals() {
    assertNotNull(eventEntityRepository.findById(1L));
    assertEquals(1, eventEntityRepository.findAll().stream().findFirst().orElseThrow().getId());
  }

  @Test
  void testBulkEventEntityRepositoryRecordNotExist() {
    Optional<EventEntity> eventEntity = eventEntityRepository.findById(999L);
    assertThrows(NoSuchElementException.class, eventEntity::orElseThrow);
  }

  @Test
  void testBulkEventEntityRepositoryGetAllFields() {
    List<EventEntity> eventEntityList = eventEntityRepository.findByContent(CONTENT);
    assertDoesNotThrow(() -> eventEntityList.stream().findFirst());
    assertEquals(SIGNATURE, eventEntityRepository.findByContent(CONTENT).stream().findFirst().orElseThrow().getSignature());
    assertEquals(EVENT_ID, eventEntityRepository.findByContent(CONTENT).stream().findFirst().orElseThrow().getEventIdString());
    assertEquals(PUB_KEY, eventEntityRepository.findByContent(CONTENT).stream().findFirst().orElseThrow().getPubKey());
    assertEquals(CONTENT, eventEntityRepository.findByContent(CONTENT).stream().findFirst().orElseThrow().getContent());
    assertEquals(KIND, eventEntityRepository.findByContent(CONTENT).stream().findFirst().orElseThrow().getKind());
    assertEquals(NIP, eventEntityRepository.findByContent(CONTENT).stream().findFirst().orElseThrow().getNip());
    
    assertEquals(CREATED_AT, eventEntityRepository.findByContent(CONTENT).stream().findFirst().orElseThrow().getCreatedAt());
  }
}
