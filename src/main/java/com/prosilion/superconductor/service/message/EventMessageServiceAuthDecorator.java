package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.event.EventServiceAuthDecorator;
import com.prosilion.superconductor.service.event.EventServiceIF;
import com.prosilion.superconductor.service.okresponse.ClientResponseService;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public class EventMessageServiceAuthDecorator<T extends EventMessage> implements MessageService<T> {
  private final EventMessageService<T> eventMessageService;
  private final EventServiceAuthDecorator<T> authenticatedEventService;

  @Autowired
  public EventMessageServiceAuthDecorator(
      EventServiceIF<T> eventService,
      ClientResponseService clientResponseService,
      EventServiceAuthDecorator<T> authenticatedEventService) {
    this.eventMessageService = new EventMessageService<>(eventService, clientResponseService);
    this.authenticatedEventService = authenticatedEventService;
  }

  public void processIncoming(@NotNull T eventMessage, @NonNull String sessionId) {
    log.info("AUTHENTICATED EVENT message NIP: {}", eventMessage.getNip());
    log.info("AUTHENTICATED EVENT message type: {}", eventMessage.getEvent());
    try {
      authenticatedEventService.processIncomingEvent(eventMessage, sessionId);
    } catch (NoSuchElementException e) {
      log.info("AUTHENTICATED EVENT message failed session authentication");
      eventMessageService.processNotOkClientResponse(eventMessage, sessionId, String.format("restricted: session [%s] has not been authenticated", sessionId));
      return;
    }
    eventMessageService.processOkClientResponse(eventMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return eventMessageService.getCommand();
  }
}