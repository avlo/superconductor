package com.prosilion.superconductor.service.message.event;

import com.prosilion.superconductor.service.message.MessageService;
import lombok.NonNull;
import nostr.event.message.EventMessage;

public interface EventMessageServiceIF<T extends EventMessage> extends MessageService<T> {
  void processOkClientResponse(T eventMessage, @NonNull String sessionId);
  void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
