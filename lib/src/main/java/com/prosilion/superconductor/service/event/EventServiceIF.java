package com.prosilion.superconductor.service.event;

import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.message.EventMessage;

public interface EventServiceIF<T extends GenericEventDtoIF> {
  <U extends EventMessage> void processIncomingEvent(U evemtMessage);
}
