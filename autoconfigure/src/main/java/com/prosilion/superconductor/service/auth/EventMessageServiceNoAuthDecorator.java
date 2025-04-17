package com.prosilion.superconductor.service.auth;

import com.prosilion.superconductor.service.event.standard.EventMessageService;
import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.event.EventServiceIF;
import com.prosilion.superconductor.service.message.MessageService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;

@Slf4j
public class EventMessageServiceNoAuthDecorator<T extends EventMessage> implements MessageService<T> {
  private final EventMessageService<T> eventMessageService;

  public EventMessageServiceNoAuthDecorator(EventServiceIF<GenericEvent> eventService, ClientResponseService okResponseService) {
    this.eventMessageService = new EventMessageService<>(eventService, okResponseService);
  }

  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    log.debug("EVENT message NIP: {}", eventMessage.getNip());
    log.debug("EVENT message type: {}", eventMessage.getEvent());
    eventMessageService.processIncoming(eventMessage, sessionId);
    eventMessageService.processOkClientResponse(eventMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return eventMessageService.getCommand();
  }
}
