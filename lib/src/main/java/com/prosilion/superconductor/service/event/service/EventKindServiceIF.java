package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.event.GenericEventDtoIF;

public interface EventKindServiceIF<T extends GenericEventDtoIF> {
  void processIncomingEvent(T event);
}
