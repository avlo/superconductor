package com.prosilion.superconductor.service.message.event;

import com.prosilion.nostr.message.EventMessage;
import org.springframework.lang.NonNull;

public interface EventMessageServiceIF {
  void processIncoming(EventMessage eventMessage, String sessionId);
  void processOkClientResponse(EventMessage eventMessage, @NonNull String sessionId);
  void processNotOkClientResponse(EventMessage eventMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
