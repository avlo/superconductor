package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
@Getter
abstract class AbstractEventKindPlugin implements EventKindPluginIF {
  private final RedisCache redisCache;

  public AbstractEventKindPlugin(@NonNull RedisCache redisCache) {
    this.redisCache = redisCache;
  }

  protected void save(@NonNull GenericEventKindIF event) {
    redisCache.saveEventEntity(event);
  }

  abstract public void processIncomingEvent(@NonNull GenericEventKindIF event);

  abstract public Kind getKind();
}
