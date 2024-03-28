package com.prosilion.nostrrelay.repository;

import com.prosilion.nostrrelay.dto.TextNoteEventDto;
import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import nostr.event.BaseTag;
import nostr.event.impl.GenericTag;
import nostr.event.impl.TextNoteEvent;
import nostr.id.IIdentity;
import nostr.id.Identity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@DataJpaTest(showSql = true)
class EventRepositoryTest {
  @Autowired
  EventRepository genericEventEventRepository;
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
    System.out.println("1111111111111111111");
    System.out.println(textNoteEventDto);
    System.out.println("1111111111111111111");
  }

  @Test
  void save() throws InvocationTargetException, IllegalAccessException {
    TextNoteEventEntity returnEvent = genericEventEventRepository.save(textNoteEventDto.convertDtoToEntity());
    System.out.println("222222222222222222222");
    System.out.println(returnEvent);
    System.out.println("222222222222222222222");

    TextNoteEvent textNoteEvent = genericEventEventRepository.findByContent("CONTENT").convertEntityToDto();
    System.out.println("333333333333333333333");
    System.out.println(textNoteEvent);
    System.out.println("333333333333333333333");
  }
}