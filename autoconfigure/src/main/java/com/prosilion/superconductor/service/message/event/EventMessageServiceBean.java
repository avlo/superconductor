package com.prosilion.superconductor.service.message.event;

import lombok.NonNull;
import nostr.event.message.EventMessage;

public interface EventMessageServiceBean<T extends EventMessage> {
  void processIncoming(T reqMessage, String sessionId);
  void processOkClientResponse(T eventMessage, @NonNull String sessionId);
  void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
