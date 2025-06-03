package com.prosilion.superconductor.service.event.type;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;

@Slf4j
@Getter
public abstract class AbstractNonPublishingEventTypePlugin<T extends GenericEvent> extends AbstractEventTypePlugin<T> {
  public AbstractNonPublishingEventTypePlugin(@NonNull RedisCache<T> redisCache) {
    super(redisCache);
  }

  public void processIncomingEvent(@NonNull T event) {
    processIncomingNonPublishingEventType(event);
  }

  abstract public void processIncomingNonPublishingEventType(@NonNull T event);
}
