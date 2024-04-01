package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.dto.TextNoteEventDto;
import nostr.event.BaseTag;
import nostr.event.impl.GenericTag;
import nostr.id.IIdentity;
import nostr.id.Identity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@DataJpaTest
class EventRepositoryTest {

  @Autowired
  EventRepository eventRepository;
  TextNoteEventDto textNoteEventDto;

  @BeforeEach
  void setUp() {
    List<BaseTag> tags = List.of(
        GenericTag.create("e", 1, "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"),
        GenericTag.create("p", 1, "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984")
    );
    IIdentity sender = Identity.getInstance();
    textNoteEventDto = new TextNoteEventDto(sender.getPublicKey(), tags, "CONTENT");
    textNoteEventDto.setId("ID");
  }

  @Test
  void saveEventTest() {
    Assertions.assertDoesNotThrow(() -> eventRepository.save(textNoteEventDto.convertDtoToEntity()));
  }

  @Test
  void convertReturnedEvenToDtoTest() throws InvocationTargetException, IllegalAccessException {
    eventRepository.save(textNoteEventDto.convertDtoToEntity());
    Assertions.assertTrue(eventRepository.findByContent("CONTENT").isPresent());
  }

  @Test
  void notFoundEntityTest() throws InvocationTargetException, IllegalAccessException {
    eventRepository.save(textNoteEventDto.convertDtoToEntity());
    Assertions.assertFalse(eventRepository.findByContent("CONTENT_SHOULD_NOT_FIND").isPresent());
  }

  @Test
  void entityToDtoConversionTest() throws InvocationTargetException, IllegalAccessException {
    eventRepository.save(textNoteEventDto.convertDtoToEntity());
    Assertions.assertTrue(eventRepository.findByContent("CONTENT").get().getContent().contains("CONTENT"));
  }
}