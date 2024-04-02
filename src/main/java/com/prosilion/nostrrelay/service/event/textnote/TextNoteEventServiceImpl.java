package com.prosilion.nostrrelay.service.event.textnote;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.BaseTagDto;
import com.prosilion.nostrrelay.dto.TextNoteEventDto;
import com.prosilion.nostrrelay.entity.BaseTagEntity;
import com.prosilion.nostrrelay.repository.BaseTagRepository;
import com.prosilion.nostrrelay.repository.EventRepository;
import com.prosilion.nostrrelay.service.event.EventServiceImpl;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.base.IEvent;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;
import nostr.event.tag.EventTag;
import nostr.id.Identity;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Log
@Getter
public class TextNoteEventServiceImpl<T extends EventMessage> extends EventServiceImpl<T> {
  private final EventRepository eventRepository;
  private final BaseTagRepository baseTagRepository;

  public TextNoteEventServiceImpl(T eventMessage) {
    super(eventMessage);
    eventRepository = ApplicationContextProvider.getApplicationContext().getBean(EventRepository.class);
    baseTagRepository = ApplicationContextProvider.getApplicationContext().getBean(BaseTagRepository.class);
  }

  @Override
  public IEvent processIncoming() throws InvocationTargetException, IllegalAccessException {
    GenericEvent event = (GenericEvent) getEventMessage().getEvent();
    NIP01<TextNoteEvent> nip01EventNIP01 = new NIP01<>(Identity.getInstance(event.getPubKey().toString()));
    NIP01<TextNoteEvent> textNoteEvent = nip01EventNIP01.createTextNoteEvent(event.getTags(), event.getContent());
    saveEntity(textNoteEvent.getEvent());
    saveTags(textNoteEvent.getEvent());
    return new NIP01<>(Identity.getInstance()).createTextNoteEvent(event.getTags(), getEventMessage().toString()).getEvent();
  }

  private void saveEntity(TextNoteEvent event) throws InvocationTargetException, IllegalAccessException, NoResultException {
    TextNoteEventDto textNoteEventDto = new TextNoteEventDto(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    textNoteEventDto.setId(getEventMessage().getEvent().getId());
    textNoteEventDto.setCreatedAt(event.getCreatedAt());
    textNoteEventDto.setSignature(event.getSignature());
    Optional.of(eventRepository.save(textNoteEventDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
  }

  private void saveTags(TextNoteEvent event) throws InvocationTargetException, IllegalAccessException, NoResultException {
    for (BaseTag baseTag : event.getTags()) {
      BaseTagDto dto = new BaseTagDto(((EventTag) baseTag).getIdEvent());
      dto.setKey(baseTag.getCode());
      BaseTagEntity entity = dto.convertDtoToEntity();
      Optional.of(baseTagRepository.save(entity)).orElseThrow(NoResultException::new);
    }
  }
}
