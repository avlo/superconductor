package com.prosilion.superconductor.service.message.event.standard;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventMessageService implements EventMessageServiceIF {
  private final EventServiceIF eventService;
  private final ClientResponseService clientResponseService;

  public EventMessageService(@NonNull EventServiceIF eventService, @NonNull ClientResponseService clientResponseService) {
    this.eventService = eventService;
    this.clientResponseService = clientResponseService;
  }

  @Override
  public void processIncoming(@NonNull EventMessage eventMessage, @NonNull String sessionId) {
    eventService.processIncomingEvent(eventMessage);
    processOkClientResponse(eventMessage, sessionId);
  }

  public void processOkClientResponse(@NonNull EventMessage eventMessage, @NonNull String sessionId) {
    clientResponseService.processOkClientResponse(sessionId, eventMessage);
  }

  public void processNotOkClientResponse(@NonNull EventMessage eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNotOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()), errorMessage);
  }
}
