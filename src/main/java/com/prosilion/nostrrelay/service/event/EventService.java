package com.prosilion.nostrrelay.service.event;

import com.google.gson.Gson;
import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.EventDto;
import com.prosilion.nostrrelay.entity.EventEntity;
import com.prosilion.nostrrelay.repository.EventEntityRepository;
import com.prosilion.nostrrelay.service.event.join.EventEntityTagEntityService;
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
public class EventService<T extends EventMessage> implements EventServiceIF<T> {
  private final EventEntityTagEntityService eventEntityTagEntityService;
  private final EventEntityRepository eventEntityRepository;

  private final T eventMessage;

  public EventService(@NotNull T eventMessage) {
    this.eventMessage = eventMessage;
    eventEntityTagEntityService = ApplicationContextProvider.getApplicationContext().getBean(EventEntityTagEntityService.class);
    eventEntityRepository = ApplicationContextProvider.getApplicationContext().getBean(EventEntityRepository.class);
    log.log(Level.INFO, "EventServiceIF Constructed");
    log.log(Level.INFO, "EVENT message KIND: {0}", ((GenericEvent) eventMessage.getEvent()).getKind());
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    log.log(Level.INFO, "EVENT message JSON: {0}", new Gson().toJson(eventMessage.getEvent().toString()));
  }

  public void processIncoming() throws InvocationTargetException, IllegalAccessException {
    log.log(Level.INFO, "processing BASE EVENT...", eventMessage.getEvent().toString());
  }

  protected Long saveEventEntity(GenericEvent event) throws InvocationTargetException, IllegalAccessException, NoResultException {
    List<BaseTag> baseTagsOnly = event.getTags().stream()
        .filter(baseTag -> List.of("a", "p", "e").contains(baseTag.getCode()))
        .toList();
    event.getTags().removeAll(baseTagsOnly);

    EventDto eventToSave = new EventDto(
        event.getPubKey(),
        event.getId(),
        Kind.valueOf(event.getKind()),
        event.getNip(),
        event.getCreatedAt(),
        event.getSignature(),
        baseTagsOnly,
        event.getContent()
    );

    EventEntity savedEntity = Optional.of(eventEntityRepository.save(eventToSave.convertDtoToEntity())).orElseThrow(NoResultException::new);
    return eventEntityTagEntityService.saveBaseTags(baseTagsOnly, savedEntity.getId());
  }
}
