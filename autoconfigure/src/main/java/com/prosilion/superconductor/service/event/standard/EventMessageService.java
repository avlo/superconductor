package com.prosilion.superconductor.service.event.standard;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.event.EventMessageServiceIF;
import com.prosilion.superconductor.service.event.EventServiceIF;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;

@Slf4j
public class EventMessageService<T extends EventMessage> implements EventMessageServiceIF<T> {
  @Getter
  public final String command = "EVENT";
  private final EventServiceIF<GenericEvent> eventService;
  private final ClientResponseService clientResponseService;

  public EventMessageService(EventServiceIF<GenericEvent> eventService, ClientResponseService clientResponseService) {
    this.eventService = eventService;
    this.clientResponseService = clientResponseService;
  }

  @Override
  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    eventService.processIncomingEvent(eventMessage);
  }

  public void processOkClientResponse(@NonNull T eventMessage, @NonNull String sessionId) {
    clientResponseService.processOkClientResponse(sessionId, eventMessage);
  }

  public void processNotOkClientResponse(@NonNull T eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNotOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()), errorMessage);
  }
}
