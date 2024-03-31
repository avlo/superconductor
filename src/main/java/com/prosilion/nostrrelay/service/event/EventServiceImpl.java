package com.prosilion.nostrrelay.service.event;

import com.google.gson.Gson;
import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.TextNoteEventDto;
import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import com.prosilion.nostrrelay.repository.EventRepository;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.base.IEvent;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import nostr.id.Identity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Getter
public class EventServiceImpl<T extends EventMessage> implements EventService<T> {
  private final EventRepository eventRepository;
  private final T eventMessage;

  public EventServiceImpl(@NotNull T eventMessage) {
    this.eventMessage = eventMessage;
    eventRepository = ApplicationContextProvider.getApplicationContext().getBean(EventRepository.class);
    log.log(Level.INFO, "EventService Constructed");
    log.log(Level.INFO, "EVENT message KIND: {0}", ((GenericEvent) eventMessage.getEvent()).getKind());
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    log.log(Level.INFO, "EVENT message JSON: {0}", new Gson().toJson(eventMessage.getEvent().toString()));
  }

  public IEvent processIncoming() throws InvocationTargetException, IllegalAccessException {
    log.log(Level.INFO, "processing BASE EVENT...", eventMessage.getEvent().toString());
    return new NIP01<>(Identity.getInstance()).createTextNoteEvent("******************* SERVER CONFIRMS PROCESSED, BASE *******************").getEvent();
  }

  protected TextNoteEventEntity save(PublicKey pubKey, List<BaseTag> tags, String content) throws InvocationTargetException, IllegalAccessException, NoResultException {
    TextNoteEventDto textNoteEventDto = new TextNoteEventDto(pubKey, tags, content);
    textNoteEventDto.setId(getEventMessage().getEvent().getId());
    return Optional.of(eventRepository.save(textNoteEventDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
  }
}
