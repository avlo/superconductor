package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.repository.EventEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.CONTENT;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.CREATED_AT;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.EVENT_ID;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.KIND;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.NIP;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.PUB_KEY;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.SIGNATURE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(
    showSql = false  // true by default
    ,
    includeFilters = @ComponentScan.Filter( // quicker tests, allegedly
        type = FilterType.ASSIGNABLE_TYPE,
        classes = EventEntityRepository.class)
)
@Sql(scripts = {"/bulk_data.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS) // class level @Sql
@DirtiesContext
class BulkEventEntityRepositoryIT {
  @Autowired
  EventEntityRepository eventEntityRepository;

  @Test
  void getCountEquals1() {
    assertEquals(1, eventEntityRepository.findAll().size());
  }

  @Test
  void testIdEquals1() {
    assertNotNull(eventEntityRepository.findById(1L));
    assertEquals(1, eventEntityRepository.findAll().stream().findFirst().orElseThrow().getId());
  }

  @Test
  void testRecordNotExist() {
    Optional<EventEntity> eventEntity = eventEntityRepository.findById(0L);
    assertThrows(NoSuchElementException.class, eventEntity::orElseThrow);
  }

  @Test
  void testGetAllFields() {
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