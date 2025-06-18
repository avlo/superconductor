package com.prosilion.superconductor.service.message.event;

import com.prosilion.superconductor.service.message.MessageServiceIF;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.message.EventMessage;

public interface AutoConfigEventMessageServiceIF<T extends EventMessage> extends MessageServiceIF<T> {
  void processOkClientResponse(T eventMessage, @NonNull String sessionId);
  void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
