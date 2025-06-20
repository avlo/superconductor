package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;
import lombok.Getter;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class AbstractNonPublishingEventKindPlugin<T extends GenericEventKindIF> extends AbstractEventKindPlugin<T> {
  public AbstractNonPublishingEventKindPlugin(@NonNull RedisCache<T> redisCache) {
    super(redisCache);
  }

  public void processIncomingEvent(@NonNull T event) {
    processIncomingNonPublishingEventType(event);
  }

  abstract public void processIncomingNonPublishingEventType(@NonNull T event);
}
