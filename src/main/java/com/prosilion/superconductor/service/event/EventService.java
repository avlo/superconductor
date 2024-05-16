package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import com.prosilion.superconductor.repository.EventEntityRepository;
import com.prosilion.superconductor.service.EventNotifierEngine;
import com.prosilion.superconductor.service.request.SubscriberService;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.api.NIP01;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Getter
@Service
public class EventService<T extends GenericEvent> {
  private final EventEntityTagEntityService eventEntityTagEntityService;
  private final EventEntityRepository eventEntityRepository;
  private final EventNotifierEngine<T> eventNotifierEngine;
  private final SubscriberService subscriberService;
  private final ApplicationEventPublisher publisher;

  @Autowired
  public EventService(
      EventEntityTagEntityService eventEntityTagEntityService,
      EventEntityRepository eventEntityRepository,
      EventNotifierEngine<T> eventNotifierEngine,
      ApplicationEventPublisher publisher,
      SubscriberService subscriberService) {
    this.publisher = publisher;
    this.eventEntityTagEntityService = eventEntityTagEntityService;
    this.eventEntityRepository = eventEntityRepository;
    this.eventNotifierEngine = eventNotifierEngine;
    this.subscriberService = subscriberService;
  }

  protected Long saveEventEntity(GenericEvent event) {
    EventDto eventToSave = new EventDto(
        event.getPubKey(),
        event.getId(),
        Kind.valueOf(event.getKind()),
        event.getNip(),
        event.getCreatedAt(),
        event.getSignature(),
        event.getTags(),
        event.getContent()
    );

    EventEntity savedEntity = Optional.of(eventEntityRepository.save(eventToSave.convertDtoToEntity())).orElseThrow(NoResultException::new);
    eventEntityTagEntityService.saveTags(event, savedEntity.getId());
    return savedEntity.getId();
  }

  protected void publishEvent(Long id, T event) {
    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<>(id, event, Kind.valueOf(event.getKind())));
  }

  @EventListener
  public void broadcastToClients(FireNostrEvent<T> fireNostrEvent) {
    EventMessage message = NIP01.createEventMessage(fireNostrEvent.event(), String.valueOf(fireNostrEvent.subscriberId()));
    Subscriber subscriber = subscriberService.get(fireNostrEvent.subscriberId());
    BroadcastMessageEvent<EventMessage> event = new BroadcastMessageEvent<>(subscriber.getSessionId(), message);
    publisher.publishEvent(event);
  }
}
