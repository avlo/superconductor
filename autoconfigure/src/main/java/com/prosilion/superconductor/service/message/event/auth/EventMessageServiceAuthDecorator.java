package com.prosilion.superconductor.service.message.event.auth;

import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.message.event.EventMessageServiceBean;
import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;

@Slf4j
public class EventMessageServiceAuthDecorator<T extends EventMessage> implements EventMessageServiceIF<T> {
  public final String command = "EVENT";

  private final EventMessageServiceBean<T> eventMessageServiceBean;
  private final AuthEntityService authEntityService;

  public EventMessageServiceAuthDecorator(
      @NonNull EventMessageServiceBean<T> eventMessageServiceBean,
      @NonNull AuthEntityService authEntityService) {
    this.eventMessageServiceBean = eventMessageServiceBean;
    this.authEntityService = authEntityService;
  }

  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    log.debug("AUTHENTICATED EVENT message NIP: {}", eventMessage.getNip());
    log.debug("AUTHENTICATED EVENT message type: {}", eventMessage.getEvent());
    try {
      authEntityService.findAuthEntityBySessionId(sessionId).orElseThrow();
    } catch (NoSuchElementException e) {
      log.debug("AUTHENTICATED EVENT message failed session authentication");
      processNotOkClientResponse(eventMessage, sessionId, String.format("restricted: session [%s] has not been authenticated", sessionId));
      return;
    }
    eventMessageServiceBean.processIncoming(eventMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return command;
  }

  @Override
  public void processOkClientResponse(T eventMessage, @NonNull String sessionId) {
    eventMessageServiceBean.processOkClientResponse(eventMessage, sessionId);
  }

  @Override
  public void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    eventMessageServiceBean.processNotOkClientResponse(eventMessage, sessionId, errorMessage);
  }
}
