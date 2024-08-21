package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.event.EventServiceIF;
import com.prosilion.superconductor.service.okresponse.ClientResponseService;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;

@Slf4j
public class EventMessageService<T extends EventMessage> implements MessageService<T> {
  @Getter
  public final String command = "EVENT";
  private final EventServiceIF<T> eventService;
  private final ClientResponseService clientResponseService;

  public EventMessageService(EventServiceIF<T> eventService, ClientResponseService clientResponseService) {
    this.eventService = eventService;
    this.clientResponseService = clientResponseService;
  }

  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    eventService.processIncomingEvent(eventMessage);
  }

  protected void processOkClientResponse(@NonNull T eventMessage, @NonNull String sessionId) {
    clientResponseService.processOkClientResponse(sessionId, eventMessage);
  }

  protected void processNotOkClientResponse(@NonNull T eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNotOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()), errorMessage);
  }
}