package com.prosilion.superconductor.service.event;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.message.EventMessage;

public interface EventServiceIF<T extends GenericEventKindIF> {
  <U extends EventMessage> void processIncomingEvent(U evemtMessage);
}
