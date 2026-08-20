package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import lombok.NonNull;

public interface DeleteEventServiceIF {
  void processIncomingEvent(@NonNull EventIF eventIF, @NonNull Relay relay);
}
