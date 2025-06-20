package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
@Getter
public abstract class AbstractNonPublishingEventKindPlugin extends AbstractEventKindPlugin {
  public AbstractNonPublishingEventKindPlugin(@NonNull RedisCache redisCache) {
    super(redisCache);
  }

  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
    processIncomingNonPublishingEventType(event);
  }

  abstract public void processIncomingNonPublishingEventType(@NonNull GenericEventKindIF event);
}
