package com.prosilion.superconductor.service.event;

import nostr.event.Kind;
import nostr.event.message.EventMessage;

public interface EventServiceIF<T extends EventMessage> {
  void processIncoming(T eventMessage);
  Kind getKind();
}
