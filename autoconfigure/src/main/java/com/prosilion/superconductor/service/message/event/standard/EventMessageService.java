package com.prosilion.superconductor.service.message.event.standard;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.service.event.EventServiceIF;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.message.EventMessage;

@Slf4j
public class EventMessageService<T extends EventMessage> implements EventMessageServiceIF<T> {
  private final EventServiceIF<GenericEventDtoIF> eventService;
  private final ClientResponseService clientResponseService;

  public EventMessageService(@NonNull EventServiceIF<GenericEventDtoIF> eventService, @NonNull ClientResponseService clientResponseService) {
    this.eventService = eventService;
    this.clientResponseService = clientResponseService;
  }

  @Override
  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    eventService.processIncomingEvent(eventMessage);
    processOkClientResponse(eventMessage, sessionId);
  }

  public void processOkClientResponse(@NonNull T eventMessage, @NonNull String sessionId) {
    clientResponseService.processOkClientResponse(sessionId, eventMessage);
  }

  public void processNotOkClientResponse(@NonNull T eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNotOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()), errorMessage);
  }
}
