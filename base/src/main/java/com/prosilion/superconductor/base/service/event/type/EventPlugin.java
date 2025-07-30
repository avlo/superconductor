package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;

public class EventPlugin implements EventPluginIF {
  private final CacheServiceIF cacheServiceIF;

  public EventPlugin(CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public void processIncomingEvent(EventIF event) {
    cacheServiceIF.save(event);
  }
}
