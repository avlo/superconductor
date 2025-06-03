package com.prosilion.superconductor.service.event.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;

@Slf4j
@Getter
abstract class AbstractEventTypePlugin<T extends GenericEvent> implements EventTypePlugin<T> {
  private final RedisCache<T> redisCache;

  public AbstractEventTypePlugin(@NonNull RedisCache<T> redisCache) {
    this.redisCache = redisCache;
  }

  protected void save(@NonNull T event) {
    redisCache.saveEventEntity(event);
  }

  abstract public void processIncomingEvent(@NonNull T event);
  abstract public Kind getKind();
}
