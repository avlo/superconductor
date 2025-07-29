package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.EventIF;

public interface EventPluginIF {
  void processIncomingEvent(EventIF event);
}
