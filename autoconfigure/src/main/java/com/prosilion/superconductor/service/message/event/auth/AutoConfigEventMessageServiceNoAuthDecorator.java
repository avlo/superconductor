package com.prosilion.superconductor.service.message.event.auth;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AutoConfigEventMessageServiceNoAuthDecorator<T extends EventMessage> implements AutoConfigEventMessageServiceIF<T> {
  private final EventMessageServiceIF<T> eventMessageServiceIF;

  public AutoConfigEventMessageServiceNoAuthDecorator(@NonNull EventMessageServiceIF<T> eventMessageServiceIF) {
    this.eventMessageServiceIF = eventMessageServiceIF;
  }

  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    log.debug("EVENT message type: {}", eventMessage.getEvent());
    eventMessageServiceIF.processIncoming(eventMessage, sessionId);
  }

  @Override
  public void processOkClientResponse(T eventMessage, @NonNull String sessionId) {
    eventMessageServiceIF.processOkClientResponse(eventMessage, sessionId);
  }

  @Override
  public void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    eventMessageServiceIF.processNotOkClientResponse(eventMessage, sessionId, errorMessage);
  }

  @Override
  public Command getCommand() {
    return Command.EVENT;
  }
}
