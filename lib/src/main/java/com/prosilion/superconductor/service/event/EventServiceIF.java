package com.prosilion.superconductor.service.event;

import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;

public interface EventServiceIF<T extends GenericEvent> {
  <U extends EventMessage> void processIncomingEvent(U evemtMessage);
}
