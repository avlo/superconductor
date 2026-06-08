package com.prosilion.superconductor.autoconfigure.base.service.message.event;

import com.prosilion.nostr.message.EventMessage;
import lombok.NonNull;

public interface EventMessageServiceIF {
  void processIncoming(EventMessage eventMessage, String sessionId);
  void processOkClientResponse(EventMessage eventMessage, @NonNull String sessionId);
  void processNotOkClientResponse(EventMessage eventMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
