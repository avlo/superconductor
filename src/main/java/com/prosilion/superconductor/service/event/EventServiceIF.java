package com.prosilion.superconductor.service.event;

import nostr.event.message.EventMessage;

public interface EventServiceIF<T extends EventMessage> {
  void processIncomingEvent(T eventMessage);
//  Kind getKind();
}
