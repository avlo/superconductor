package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
final public class EventPlugin implements EventPluginIF {
  private final RedisCache redisCache;

  @Autowired
  public EventPlugin(RedisCache redisCache) {
    this.redisCache = redisCache;
  }

  @Override
  public void processIncomingEvent(GenericEventKindIF event) {
    redisCache.saveEventEntity(event);
  }
}
