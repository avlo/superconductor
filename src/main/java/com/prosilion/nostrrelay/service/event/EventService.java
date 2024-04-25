package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.dto.EventDto;
import com.prosilion.nostrrelay.entity.EventEntity;
import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.BroadcastMessageEvent;
import com.prosilion.nostrrelay.repository.EventEntityRepository;
import com.prosilion.nostrrelay.service.EventNotifierEngine;
import com.prosilion.nostrrelay.service.event.join.EventEntityTagEntityService;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.BaseMessage;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log
@Getter
@Service
public class EventService<T extends GenericEvent> {
  private final EventEntityTagEntityService eventEntityTagEntityService;
  private final EventEntityRepository eventEntityRepository;
  private final EventNotifierEngine<T> eventNotifierEngine;
  private final ApplicationEventPublisher publisher;

  @Autowired
  public EventService(EventEntityTagEntityService eventEntityTagEntityService, EventEntityRepository eventEntityRepository, EventNotifierEngine<T> eventNotifierEngine, ApplicationEventPublisher publisher) {
    this.publisher = publisher;
    this.eventEntityTagEntityService = eventEntityTagEntityService;
    this.eventEntityRepository = eventEntityRepository;
    this.eventNotifierEngine = eventNotifierEngine;
  }

  protected Long saveEventEntity(GenericEvent event) {
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

  protected void publishEvent(Long id, T event) {
    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<>(id, event, Kind.valueOf(event.getKind())));
  }

  protected <U extends BaseMessage> void publishEvent(BroadcastMessageEvent<U> messageEvent) {
    publisher.publishEvent(messageEvent);
  }
}
