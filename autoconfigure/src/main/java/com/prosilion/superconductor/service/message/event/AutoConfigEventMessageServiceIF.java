package com.prosilion.superconductor.service.message.event;

import com.prosilion.superconductor.service.message.MessageServiceIF;
import lombok.NonNull;
import nostr.event.message.EventMessage;

public interface AutoConfigEventMessageServiceIF<T extends EventMessage> extends MessageServiceIF<T> {
  void processOkClientResponse(T eventMessage, @NonNull String sessionId);
  void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
