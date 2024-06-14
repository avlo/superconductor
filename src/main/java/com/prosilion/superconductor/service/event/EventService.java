package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.NotifierService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService<T extends GenericEvent> {
  private final NotifierService<T> notifierService;
  private final RedisCache<T> redisCache;

  @Autowired
  public EventService(NotifierService<T> notifierService, RedisCache<T> redisCache) {
    this.notifierService = notifierService;
    this.redisCache = redisCache;
  }

  protected Long saveEventEntity(@NonNull T event) {
    return redisCache.saveEventEntity(event);
  }

  protected void publishEvent(@NonNull Long id, @NonNull T event) {
    notifierService.nostrEventHandler(
        new AddNostrEvent<>(event));
  }
}