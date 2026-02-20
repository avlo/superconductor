package com.prosilion.superconductor.base.service.event.plugin;

import com.prosilion.nostr.event.EventIF;

public interface EventPluginIF {
  void processIncomingEvent(EventIF event);
}
