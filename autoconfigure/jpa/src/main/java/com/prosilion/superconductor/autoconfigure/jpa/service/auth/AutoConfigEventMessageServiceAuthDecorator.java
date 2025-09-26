package com.prosilion.superconductor.autoconfigure.jpa.service.auth;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthEntityServiceIF;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AutoConfigEventMessageServiceAuthDecorator implements AutoConfigEventMessageServiceIF {
  private final EventMessageServiceIF eventMessageServiceIF;
  private final AuthEntityServiceIF authEntityServiceIF;

  public AutoConfigEventMessageServiceAuthDecorator(
      @NonNull EventMessageServiceIF eventMessageServiceIF,
      @NonNull AuthEntityServiceIF authEntityServiceIF) {
    this.eventMessageServiceIF = eventMessageServiceIF;
    this.authEntityServiceIF = authEntityServiceIF;
  }

  public void processIncoming(@NonNull EventMessage eventMessage, @NonNull String sessionId) {
    log.debug("AUTHENTICATED EVENT message type: {}", eventMessage.getEvent());
    try {
      authEntityServiceIF.findAuthEntityBySessionId(sessionId).orElseThrow();
    } catch (NoSuchElementException e) {
      log.debug("AUTHENTICATED EVENT message failed session authentication");
      processNotOkClientResponse(eventMessage, sessionId, String.format("restricted: EVENT sessionId [%s] has not been authenticated", sessionId));
      return;
    }
    eventMessageServiceIF.processIncoming(eventMessage, sessionId);
  }

  @Override
  public void processOkClientResponse(EventMessage eventMessage, @NonNull String sessionId) {
    eventMessageServiceIF.processOkClientResponse(eventMessage, sessionId);
  }

  @Override
  public void processNotOkClientResponse(EventMessage eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    eventMessageServiceIF.processNotOkClientResponse(eventMessage, sessionId, errorMessage);
  }
}
