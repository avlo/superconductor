package com.prosilion.superconductor.service.message.event;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.service.message.MessageServiceIF;
import org.springframework.lang.NonNull;

public interface AutoConfigEventMessageServiceIF extends MessageServiceIF<EventMessage> {
  void processOkClientResponse(EventMessage eventMessage, @NonNull String sessionId);
  void processNotOkClientResponse(EventMessage eventMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
