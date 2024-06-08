package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.NotifierService;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService<T extends GenericEvent> {
  private final NotifierService<T> notifierService;
  private final RedisEventEntityService<T> redisEventEntityService;

  @Autowired
  public EventService(NotifierService<T> notifierService, RedisEventEntityService<T> redisEventEntityService) {
    this.notifierService = notifierService;
    this.redisEventEntityService = redisEventEntityService;
  }

  protected Long saveEventEntity(T event) {
    return redisEventEntityService.saveEventEntity(event);
  }

  protected void publishEvent(Long id, T event) {
    notifierService.nostrEventHandler(
        new AddNostrEvent<>(Kind.valueOf(event.getKind()), id, event));
  }
}