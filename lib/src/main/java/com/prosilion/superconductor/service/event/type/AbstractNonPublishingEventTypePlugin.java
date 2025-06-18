package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.event.GenericEventDtoIF;
import lombok.Getter;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class AbstractNonPublishingEventTypePlugin<T extends GenericEventDtoIF> extends AbstractEventKindPlugin<T> {
  public AbstractNonPublishingEventTypePlugin(@NonNull RedisCache<T> redisCache) {
    super(redisCache);
  }

  public void processIncomingEvent(@NonNull T event) {
    processIncomingNonPublishingEventType(event);
  }

  abstract public void processIncomingNonPublishingEventType(@NonNull T event);
}
