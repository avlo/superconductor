package com.prosilion.superconductor.autoconfigure.base.service.message.event.auth;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.base.service.event.auth.AuthPersistantIF;
import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AutoConfigEventMessageServiceAuthDecorator<T, U extends AuthPersistantIF> implements AutoConfigEventMessageServiceIF {
  private final EventMessageServiceIF eventMessageServiceIF;
  private final AuthPersistantServiceIF<T, U> authPersistantServiceIF;

  public AutoConfigEventMessageServiceAuthDecorator(
      @NonNull EventMessageServiceIF eventMessageServiceIF,
      @NonNull AuthPersistantServiceIF<T, U> authPersistantServiceIF) {
    this.eventMessageServiceIF = eventMessageServiceIF;
    this.authPersistantServiceIF = authPersistantServiceIF;
  }

  public void processIncoming(@NonNull EventMessage eventMessage, @NonNull String sessionId) {
    log.debug("AUTHENTICATED EVENT message type: {}", eventMessage.getEvent());
    try {
      authPersistantServiceIF.findAuthPersistantBySessionId(sessionId).orElseThrow();
    } catch (NoSuchElementException e) {
      log.debug("AUTHENTICATED EVENT message failed session authentication");
      processNotOkClientResponse(eventMessage, sessionId, String.format("restricted: EVENT sessionId [%s] has not been authenticated", sessionId));
      return;
    }
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
