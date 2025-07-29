package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.CacheIF;

public class EventPlugin implements EventPluginIF {
  private final CacheIF cacheIF;

  public EventPlugin(CacheIF cacheIF) {
    this.cacheIF = cacheIF;
  }

  @Override
  public void processIncomingEvent(EventIF event) {
    cacheIF.save(event);
  }
}
