package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import com.prosilion.superconductor.repository.EventEntityRepository;
import com.prosilion.superconductor.service.EventNotifierEngine;
import com.prosilion.superconductor.service.event.join.EventEntityBaseTagEntityService;
import com.prosilion.superconductor.service.event.join.EventEntityGenericTagEntityService;
import com.prosilion.superconductor.service.request.SubscriberService;
import com.prosilion.superconductor.util.BaseTagValueMapper;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.api.NIP01;
import nostr.event.BaseMessage;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import nostr.event.tag.EventTag;
import nostr.event.tag.PubKeyTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Service
public class EventService<T extends GenericEvent> {
  private final EventEntityBaseTagEntityService eventEntityBaseTagEntityService;
  private final EventEntityGenericTagEntityService<GenericTagEntity> eventEntityGenericTagEntityService;
  private final EventEntityRepository eventEntityRepository;
  private final EventNotifierEngine<T> eventNotifierEngine;
  private final SubscriberService subscriberService;
  private final ApplicationEventPublisher publisher;

  @Autowired
  public EventService(
      EventEntityBaseTagEntityService eventEntityBaseTagEntityService,
      EventEntityGenericTagEntityService<GenericTagEntity> eventEntityGenericTagEntityService,
      EventEntityRepository eventEntityRepository,
      EventNotifierEngine<T> eventNotifierEngine,
      ApplicationEventPublisher publisher,
      SubscriberService subscriberService) {
    this.publisher = publisher;
    this.eventEntityBaseTagEntityService = eventEntityBaseTagEntityService;
    this.eventEntityGenericTagEntityService = eventEntityGenericTagEntityService;
    this.eventEntityRepository = eventEntityRepository;
    this.eventNotifierEngine = eventNotifierEngine;
    this.subscriberService = subscriberService;
  }

  protected Long saveEventEntity(GenericEvent event) {
    List<BaseTag> baseTagsOnly = event.getTags().stream()
        .filter(baseTag -> List.of("a", "p", "e").contains(baseTag.getCode()))
        .toList();

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
    eventEntityGenericTagEntityService.saveGenericTags(event, savedEntity.getId());
    eventEntityBaseTagEntityService.saveBaseTags(baseTagsOnly.stream().map(this::getValue).toList(), savedEntity.getId());
    return savedEntity.getId();
  }

  private BaseTagValueMapper getValue(BaseTag baseTag) {
    if (baseTag.getCode().equals("e")) // event
      return new BaseTagValueMapper(baseTag, ((EventTag) baseTag).getIdEvent());
    return new BaseTagValueMapper(baseTag, ((PubKeyTag) baseTag).getPublicKey().toString());
  }

  protected void publishEvent(Long id, T event) {
    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<>(id, event, Kind.valueOf(event.getKind())));
  }

  @EventListener
  public void broadcastToClients(FireNostrEvent<T> fireNostrEvent) {
    EventMessage message = NIP01.createEventMessage(fireNostrEvent.event(), String.valueOf(fireNostrEvent.subscriberId()));
    Subscriber subscriber = subscriberService.get(fireNostrEvent.subscriberId());
    BroadcastMessageEvent<EventMessage> event = new BroadcastMessageEvent<>(subscriber.getSessionId(), message);
    publishEvent(event);
  }

  public <U extends BaseMessage> void publishEvent(BroadcastMessageEvent<U> messageEvent) {
    publisher.publishEvent(messageEvent);
  }
}
