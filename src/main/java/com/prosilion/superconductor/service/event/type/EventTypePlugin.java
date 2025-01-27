package com.prosilion.superconductor.service.event.type;

import lombok.NonNull;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;

public interface EventTypePlugin<T extends GenericEvent> {
  void processIncomingEvent(@NonNull T eventMessage);
  Kind getKind();
}
