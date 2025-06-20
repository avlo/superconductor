package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.event.GenericEventKindTypeIF;

public interface EventKindTypeServiceIF<KindType, U extends GenericEventKindTypeIF> {
  void processIncomingEvent(U event);
}
