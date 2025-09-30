package com.prosilion.superconductor.autoconfigure.base.service.message.event.standard;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;

@Slf4j
public class EventMessageService implements EventMessageServiceIF {
  public static final String AUTH_REQUIRED = "auth-required: ";

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
    clientResponseService.processNotOkClientResponse(sessionId, eventMessage, Strings.concat(AUTH_REQUIRED, errorMessage));
  }
}
