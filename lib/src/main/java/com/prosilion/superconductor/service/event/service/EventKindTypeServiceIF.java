package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Type;
import com.prosilion.nostr.event.AbstractBadgeAwardEventIF;

public interface EventKindTypeServiceIF<T extends Type, U extends AbstractBadgeAwardEventIF<T>> {
  void processIncomingEvent(U event);
}
