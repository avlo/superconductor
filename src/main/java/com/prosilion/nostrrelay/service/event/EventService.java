package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.util.BaseTagValueMapper;
import com.prosilion.nostrrelay.dto.EventDto;
import com.prosilion.nostrrelay.entity.EventEntity;
import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.BroadcastMessageEvent;
import com.prosilion.nostrrelay.pubsub.FireNostrEvent;
import com.prosilion.nostrrelay.repository.EventEntityRepository;
import com.prosilion.nostrrelay.service.EventNotifierEngine;
import com.prosilion.nostrrelay.service.event.join.EventEntityTagEntityService;
import com.prosilion.nostrrelay.service.request.SubscriberService;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.java.Log;
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

@Log
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
    return eventEntityTagEntityService.saveBaseTags(baseTagsOnly.stream().map(this::getValue).toList(), savedEntity.getId());
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
  public void handleFireNostrEvent(FireNostrEvent<T> fireNostrEvent) {
    EventMessage message = NIP01.createEventMessage(fireNostrEvent.event(), String.valueOf(fireNostrEvent.subscriberId()));
    Subscriber subscriber = subscriberService.get(fireNostrEvent.subscriberId());
    BroadcastMessageEvent<EventMessage> event = new BroadcastMessageEvent<>(subscriber.getSessionId(), message);
    publishEvent(event);
  }

  public <U extends BaseMessage> void publishEvent(BroadcastMessageEvent<U> messageEvent) {
    publisher.publishEvent(messageEvent);
  }
}
