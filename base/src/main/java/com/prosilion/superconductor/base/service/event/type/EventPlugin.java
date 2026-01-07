package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventPlugin implements EventPluginIF {
  private final CacheServiceIF cacheServiceIF;

  public EventPlugin(@NonNull CacheServiceIF cacheServiceIF) {
    log.info("class {} adding:", getClass().getSimpleName());
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public void processIncomingEvent(EventIF event) {
    log.info("{} processIncomingEvent() called with event: {}", getClass().getSimpleName(), event);
    cacheServiceIF.save(event);
  }
}
