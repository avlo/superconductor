package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import lombok.NonNull;

public interface EventServiceIF {
  void processIncomingEvent(@NonNull EventMessage eventMessage, @NonNull Relay relay);
}
