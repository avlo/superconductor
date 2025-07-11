package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.base.entity.EventEntity;
import com.prosilion.superconductor.base.repository.EventEntityRepository;
import com.prosilion.superconductor.util.Factory;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(
    showSql = false  // on by default
    , includeFilters = @ComponentScan.Filter( // quicker tests, allegedly
    type = FilterType.ASSIGNABLE_TYPE,
    classes = EventEntityRepository.class)
)
@ActiveProfiles("test")
class EventEntityRepositoryIT {
  public static final String SIGNATURE = "86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546";
  public static final String EVENT_ID = Factory.generateRandomHex64String();
  public static final String PUB_KEY = Factory.createNewIdentity().getPublicKey().toHexString();
  public static final String CONTENT = "1112221111";
  public static final Integer KIND = 1;
  public static final long CREATED_AT = 1717357053050L;

  @Autowired
  EventEntityRepository eventEntityRepository;

  @BeforeEach
  void setUp() {
    eventEntityRepository.save(new EventEntity(EVENT_ID, KIND, PUB_KEY, CREATED_AT, SIGNATURE, CONTENT));
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
    assertEquals(CREATED_AT, eventEntityRepository.findByContent(CONTENT).stream().findFirst().orElseThrow().getCreatedAt());
  }

  @Test
  void testTestClassCreatedEntity() {
    assertDoesNotThrow(() -> eventEntityRepository.findByContent(CONTENT));
  }
}
