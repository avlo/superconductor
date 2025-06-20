package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.event.GenericEventKindTypeIF;

public interface EventKindTypeServiceIF<T extends GenericEventKindTypeIF> {
  void processIncomingEvent(T event);
}
