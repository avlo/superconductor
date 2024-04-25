package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.dto.EventDto;
import nostr.base.Signature;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericTag;
import nostr.id.IIdentity;
import nostr.id.Identity;
import nostr.util.NostrUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@DataJpaTest
@DirtiesContext
class TextNoteEventRepositoryTest {

  @Autowired
  EventEntityRepository repository;
  EventDto eventDto;

  @BeforeEach
  void setUp() {
    List<BaseTag> tags = List.of(
        GenericTag.create("e", 1, "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"),
        GenericTag.create("p", 1, "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984")
    );

    byte[] rawData = NostrUtil.hexToBytes("123123");
    Signature signature = new Signature();
    signature.setRawData(rawData);

    IIdentity sender = Identity.getInstance();
    eventDto = new EventDto(
        sender.getPublicKey(),
        "SOME ID",
        Kind.TEXT_NOTE,
        1,
        12345667L,
        signature,
        tags,
        "CONTENT");
    eventDto.setId("ID");
  }

  @Test
  void saveEventTest() {
    Assertions.assertDoesNotThrow(() -> repository.save(eventDto.convertDtoToEntity()));
  }

  @Test
  void convertReturnedEvenToDtoTest() throws InvocationTargetException, IllegalAccessException {
    repository.save(eventDto.convertDtoToEntity());
    Assertions.assertTrue(repository.findByContent("CONTENT").isPresent());
  }

  @Test
  void notFoundEntityTest() throws InvocationTargetException, IllegalAccessException {
    repository.save(eventDto.convertDtoToEntity());
    Assertions.assertFalse(repository.findByContent("CONTENT_SHOULD_NOT_FIND").isPresent());
  }

  @Test
  void entityToDtoConversionTest() throws InvocationTargetException, IllegalAccessException {
    repository.save(eventDto.convertDtoToEntity());
    Assertions.assertTrue(repository.findByContent("CONTENT").get().getContent().contains("CONTENT"));
  }
}