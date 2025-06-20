package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import lombok.Getter;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
abstract class AbstractEventKindPlugin<T extends GenericEventKindIF> implements EventKindPluginIF<T> {
  private final RedisCache<T> redisCache;

  public AbstractEventKindPlugin(@NonNull RedisCache<T> redisCache) {
    this.redisCache = redisCache;
  }

  protected void save(@NonNull T event) {
    redisCache.saveEventEntity(event);
  }

  abstract public void processIncomingEvent(@NonNull T event);

  abstract public Kind getKind();
}
