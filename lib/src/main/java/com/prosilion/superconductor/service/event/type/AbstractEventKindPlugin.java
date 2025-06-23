package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class AbstractEventKindPlugin implements EventKindPluginIF {
  private final RedisCache redisCache;

  @Autowired
  public AbstractEventKindPlugin(@NonNull RedisCache redisCache) {
    this.redisCache = redisCache;
  }

  public void save(@NonNull GenericEventKindIF event) {
    redisCache.saveEventEntity(event);
  }
}
