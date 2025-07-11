package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;

final public class EventPlugin implements EventPluginIF {
  private final RedisCache redisCache;

  public EventPlugin(RedisCache redisCache) {
    this.redisCache = redisCache;
  }

  @Override
  public void processIncomingEvent(GenericEventKindIF event) {
    redisCache.saveEventEntity(event);
  }
}
