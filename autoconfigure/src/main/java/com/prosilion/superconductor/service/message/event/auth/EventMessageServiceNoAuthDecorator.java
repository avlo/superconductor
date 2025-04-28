package com.prosilion.superconductor.service.message.event.auth;

import com.prosilion.superconductor.service.message.event.EventMessageServiceBean;
import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;

@Slf4j
public class EventMessageServiceNoAuthDecorator<T extends EventMessage> implements EventMessageServiceIF<T> {
  public final String command = "EVENT";
  private final EventMessageServiceBean<T> eventMessageServiceBean;

  public EventMessageServiceNoAuthDecorator(@NonNull EventMessageServiceBean<T> eventMessageServiceBean) {
    this.eventMessageServiceBean = eventMessageServiceBean;
  }

  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    log.debug("EVENT message NIP: {}", eventMessage.getNip());
    log.debug("EVENT message type: {}", eventMessage.getEvent());
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
