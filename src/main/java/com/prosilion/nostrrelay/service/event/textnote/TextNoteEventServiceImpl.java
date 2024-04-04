package com.prosilion.nostrrelay.service.event.textnote;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.event.TextNoteEventDto;
import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import com.prosilion.nostrrelay.repository.TextNoteEventRepository;
import com.prosilion.nostrrelay.service.event.EventServiceImpl;
import com.prosilion.nostrrelay.service.event.join.EventTagEntityServiceImpl;
import jakarta.persistence.NoResultException;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.base.IEvent;
import nostr.event.NIP01Event;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;
import nostr.id.Identity;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Log
public class TextNoteEventServiceImpl<T extends EventMessage> extends EventServiceImpl<T> {
  private final EventTagEntityServiceImpl eventTagEntityService;
  private final TextNoteEventRepository textNoteEventRepository;

  public TextNoteEventServiceImpl(T eventMessage) {
    super(eventMessage);
    eventTagEntityService = ApplicationContextProvider.getApplicationContext().getBean(EventTagEntityServiceImpl.class);
    textNoteEventRepository = ApplicationContextProvider.getApplicationContext().getBean(TextNoteEventRepository.class);
  }

  @Override
  public IEvent processIncoming() throws InvocationTargetException, IllegalAccessException {
    GenericEvent event = (GenericEvent) getEventMessage().getEvent();
    NIP01<TextNoteEvent> nip01EventNIP01 = new NIP01<>(Identity.getInstance(event.getPubKey().toString()));
    NIP01<TextNoteEvent> textNoteEvent = nip01EventNIP01.createTextNoteEvent(event.getTags(), event.getContent());
    Long savedEventId = saveEntity(textNoteEvent.getEvent(), getEventMessage().getEvent().getId());
    eventTagEntityService.save(textNoteEvent.getEvent(), savedEventId);
    NIP01<NIP01Event> textNoteEvent1 = new NIP01<>(Identity.getInstance()).createTextNoteEvent(event.getTags(), getEventMessage().toString());
    return textNoteEvent1.getEvent();
  }

  private Long saveEntity(GenericEvent event, String id) throws InvocationTargetException, IllegalAccessException, NoResultException {
    TextNoteEventDto textNoteEventDto = new TextNoteEventDto(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    textNoteEventDto.setId(id);
    textNoteEventDto.setCreatedAt(event.getCreatedAt());
    textNoteEventDto.setSignature(event.getSignature());
    TextNoteEventEntity textNoteEventEntity = Optional.of(textNoteEventRepository.save(textNoteEventDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
    return textNoteEventEntity.getId();
  }

  public GenericEvent findById(Long id) {
    return textNoteEventRepository.findById(id).get().convertEntityToDto();
  }
}
