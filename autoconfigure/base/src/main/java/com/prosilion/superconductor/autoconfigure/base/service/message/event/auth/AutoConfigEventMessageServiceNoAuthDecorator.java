package com.prosilion.superconductor.autoconfigure.base.service.message.event.auth;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AutoConfigEventMessageServiceNoAuthDecorator implements AutoConfigEventMessageServiceIF {
  private final EventMessageServiceIF eventMessageServiceIF;

  public AutoConfigEventMessageServiceNoAuthDecorator(@NonNull EventMessageServiceIF eventMessageServiceIF) {
    this.eventMessageServiceIF = eventMessageServiceIF;
  }

  public void processIncoming(@NonNull EventMessage eventMessage, @NonNull String sessionId) {
    log.debug("EVENT message type: {}", eventMessage.getEvent());
    eventMessageServiceIF.processIncoming(eventMessage, sessionId);
  }

  @Override
  public void processOkClientResponse(@NonNull EventMessage eventMessage, @NonNull String sessionId) {
    eventMessageServiceIF.processOkClientResponse(eventMessage, sessionId);
  }

  @Override
  public void processNotOkClientResponse(@NonNull EventMessage eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    eventMessageServiceIF.processNotOkClientResponse(eventMessage, sessionId, errorMessage);
  }
}
