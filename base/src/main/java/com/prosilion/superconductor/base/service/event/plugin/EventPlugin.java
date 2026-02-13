package com.prosilion.superconductor.base.service.event.plugin;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventPlugin implements EventPluginIF {
  private final CacheServiceIF cacheServiceIF;

  public EventPlugin(@NonNull CacheServiceIF cacheServiceIF) {
    log.debug("class is adding class: {}", cacheServiceIF.getClass().getSimpleName());
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    log.debug("processIncomingEvent() called with event\n{}", event.createPrettyPrintJson());
    cacheServiceIF.save(event);
  }
}
