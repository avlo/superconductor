package com.prosilion.nostrrelay.service.event;

import com.google.gson.Gson;
import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.event.EventDto;
import com.prosilion.nostrrelay.entity.EventEntity;
import com.prosilion.nostrrelay.repository.EventRepository;
import com.prosilion.nostrrelay.service.event.join.EventTagEntityServiceImpl;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Getter
public class EventServiceImpl<T extends EventMessage> implements EventService<T> {
  private final EventTagEntityServiceImpl eventTagEntityService;
  private final EventRepository eventRepository;

  private final T eventMessage;

  public EventServiceImpl(@NotNull T eventMessage) {
    this.eventMessage = eventMessage;
    eventTagEntityService = ApplicationContextProvider.getApplicationContext().getBean(EventTagEntityServiceImpl.class);
    eventRepository = ApplicationContextProvider.getApplicationContext().getBean(EventRepository.class);
    log.log(Level.INFO, "EventService Constructed");
    log.log(Level.INFO, "EVENT message KIND: {0}", ((GenericEvent) eventMessage.getEvent()).getKind());
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    log.log(Level.INFO, "EVENT message JSON: {0}", new Gson().toJson(eventMessage.getEvent().toString()));
  }

  public void processIncoming() throws InvocationTargetException, IllegalAccessException {
    log.log(Level.INFO, "processing BASE EVENT...", eventMessage.getEvent().toString());
  }

  protected Long saveEventEntity(GenericEvent event) throws InvocationTargetException, IllegalAccessException, NoResultException {
    List<BaseTag> baseTags = event.getTags().stream()
        .filter(baseTag -> List.of("a", "p", "e").contains(baseTag.getCode()))
        .toList();
    event.getTags().removeAll(baseTags);

    EventDto eventDto = new EventDto(
        event.getPubKey(),
        event.getId(),
        Kind.valueOf(event.getKind()),
        event.getNip(),
        event.getCreatedAt(),
        event.getSignature(),
        baseTags,
        event.getContent()
    );

    EventEntity eventEntityImpl = Optional.of(eventRepository.save(eventDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
    return eventTagEntityService.save(baseTags, eventEntityImpl.getId());
  }
}
