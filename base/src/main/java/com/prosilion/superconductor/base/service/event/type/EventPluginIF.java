package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;

public interface EventPluginIF {
  void processIncomingEvent(GenericEventKindIF event);
}
