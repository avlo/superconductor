package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.event.GenericEventKindTypeIF;

public interface EventKindTypeServiceIF {
  void processIncomingEvent(GenericEventKindTypeIF event);
}
