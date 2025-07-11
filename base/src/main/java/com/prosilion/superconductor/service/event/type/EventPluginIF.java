package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;

public interface EventPluginIF {
  void processIncomingEvent(GenericEventKindIF event);
}
