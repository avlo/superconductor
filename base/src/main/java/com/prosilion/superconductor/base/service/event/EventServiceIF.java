package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.message.EventMessage;

public interface EventServiceIF {
  void processIncomingEvent(EventMessage eventMessage);
}
