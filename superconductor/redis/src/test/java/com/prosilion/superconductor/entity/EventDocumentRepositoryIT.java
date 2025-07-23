package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
import com.prosilion.superconductor.util.Factory;
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
    assertEquals(EVENT_ID, savedAndFetchedDocument.getEventIdString());
    assertEquals(KIND, savedAndFetchedDocument.getKind());
    assertEquals(PUB_KEY, savedAndFetchedDocument.getPubKey());
    assertEquals(CREATED_AT, savedAndFetchedDocument.getCreatedAt());
    assertEquals(CONTENT, savedAndFetchedDocument.getContent());
    assertEquals(SIGNATURE, savedAndFetchedDocument.getSignature());
  }

  @Test
  void testGetAllFields() {
    Optional<EventDocument> retrieved = eventDocumentRepository.findByEventIdString(EVENT_ID);
    EventDocument byEventIdString = retrieved.orElseThrow();
    assertEquals(EVENT_ID, byEventIdString.getEventIdString());
    assertEquals(KIND, byEventIdString.getKind());
    assertEquals(PUB_KEY, byEventIdString.getPubKey());
    assertEquals(CREATED_AT, byEventIdString.getCreatedAt());
    assertEquals(CONTENT, byEventIdString.getContent());
    assertEquals(SIGNATURE, byEventIdString.getSignature());
  }

  @Test
  void testRecordNotExist() {
    Optional<EventDocument> eventEntity = eventDocumentRepository.findByEventIdString(Factory.generateRandomHex64String());
    assertThrows(NoSuchElementException.class, eventEntity::orElseThrow);
  }
}
