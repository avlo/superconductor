package com.prosilion.superconductor.autoconfigure.base.service.message.event;

import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import lombok.NonNull;

public interface EventMessageServiceIF {
  void processIncoming(@NonNull EventMessage eventMessage, @NonNull String sessionId, @NonNull Relay relay);
  void processOkClientResponse(@NonNull EventMessage eventMessage, @NonNull String sessionId);
  void processNotOkClientResponse(@NonNull EventMessage eventMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
