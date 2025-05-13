package com.prosilion.superconductor.service.message.event.auth;

import com.prosilion.superconductor.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;

@Slf4j
public class AutoConfigEventMessageServiceNoAuthDecorator<T extends EventMessage> implements AutoConfigEventMessageServiceIF<T> {
  public final String command = "EVENT";
  private final EventMessageServiceIF<T> eventMessageServiceIF;

  public AutoConfigEventMessageServiceNoAuthDecorator(@NonNull EventMessageServiceIF<T> eventMessageServiceIF) {
    this.eventMessageServiceIF = eventMessageServiceIF;
  }

  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    log.debug("EVENT message NIP: {}", eventMessage.getNip());
    log.debug("EVENT message type: {}", eventMessage.getEvent());
    eventMessageServiceIF.processIncoming(eventMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return command;
  }

  @Override
  public void processOkClientResponse(T eventMessage, @NonNull String sessionId) {
    eventMessageServiceIF.processOkClientResponse(eventMessage, sessionId);
  }

  @Override
  public void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    eventMessageServiceIF.processNotOkClientResponse(eventMessage, sessionId, errorMessage);
  }
}
