package com.prosilion.superconductor.redis.entity;

import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
import com.prosilion.superconductor.redis.util.Factory;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
class EventDocumentRepositoryIT {
  public static final String SIGNATURE = "86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546";
  public static final String EVENT_ID = Factory.generateRandomHex64String();
  public static final String PUB_KEY = Factory.createNewIdentity().getPublicKey().toHexString();
  public static final String CONTENT = "1112221111";
  public static final Integer KIND = 1;
  public static final long CREATED_AT = 1717357053050L;

  private final EventDocumentRepository eventDocumentRepository;

  @Autowired
  EventDocumentRepositoryIT(EventDocumentRepository eventDocumentRepository) {
    this.eventDocumentRepository = eventDocumentRepository;

    EventDocument savedAndFetchedDocument = eventDocumentRepository.save(
        EventDocument.of(
            EVENT_ID,
            KIND,
            PUB_KEY,
            CREATED_AT,
            CONTENT,
            SIGNATURE));
    assertEquals(EVENT_ID, savedAndFetchedDocument.getId());
    assertEquals(KIND, savedAndFetchedDocument.getKind().getValue());
    assertEquals(PUB_KEY, savedAndFetchedDocument.getPubKey());
    assertEquals(CREATED_AT, savedAndFetchedDocument.getCreatedAt());
    assertEquals(CONTENT, savedAndFetchedDocument.getContent());
    assertEquals(SIGNATURE, savedAndFetchedDocument.getSignature().toString());
  }

  @Test
  void testGetAllFields() {
    Optional<EventDocumentIF> retrieved = eventDocumentRepository.findByEventIdICustom(EVENT_ID);
    EventDocumentIF byEventIdString = retrieved.orElseThrow();
    assertEquals(EVENT_ID, byEventIdString.getId());
    assertEquals(KIND, byEventIdString.getKind().getValue());
    assertEquals(PUB_KEY, byEventIdString.getPublicKey().toString());
    assertEquals(CREATED_AT, byEventIdString.getCreatedAt());
    assertEquals(CONTENT, byEventIdString.getContent());
    assertEquals(SIGNATURE, byEventIdString.getSignature().toString());
  }

  @Test
  void testRecordNotExist() {
    Optional<EventDocument> eventEntity = eventDocumentRepository.findById(Factory.generateRandomHex64String());
    Optional<EventDocumentIF> eventDocumentIF = Optional.of(eventEntity).map(EventDocumentIF.class::cast);
    assertThrows(NoSuchElementException.class, eventDocumentIF::orElseThrow);
  }
}
