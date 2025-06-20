package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.event.GenericEventKindIF;

public interface EventKindServiceIF {
  void processIncomingEvent(GenericEventKindIF event);
}
