package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.NotifierService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
public class EventService<T extends GenericEvent> {
  private final EventEntityService eventEntityService;
  private final NotifierService<T> notifierService;
  private final RedisEventEntityService<T> redisEventEntityService;

  @Autowired
  public EventService(EventEntityService eventEntityService, NotifierService<T> notifierService, RedisEventEntityService<T> redisEventEntityService) {
    this.eventEntityService = eventEntityService;
    this.notifierService = notifierService;
    this.redisEventEntityService = redisEventEntityService;
  }

  protected Long saveEventEntity(T event) {
    return eventEntityService.saveEventEntity(event);
  }

  protected void publishEvent(Long id, T event) {
    AddNostrEvent<T> addNostrEvent = new AddNostrEvent<>(Kind.valueOf(event.getKind()), id, event);
    notifierService.nostrEventHandler(addNostrEvent);
  }
}