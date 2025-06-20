package com.prosilion.superconductor.service.message.event.noop;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventMessageNoOpService implements EventMessageServiceIF {
  public final String noOp;

  private final ClientResponseService clientResponseService;

  public EventMessageNoOpService(@NonNull ClientResponseService clientResponseService, @NonNull String noOp) {
    this.clientResponseService = clientResponseService;
    this.noOp = noOp;
  }

  @Override
  public void processIncoming(@NonNull EventMessage eventMessage, @NonNull String sessionId) {
    log.debug("processing incoming NOOP-EVENT: [{}]", eventMessage);
    processNotOkClientResponse(eventMessage, sessionId, noOp);
    clientResponseService.processCloseClientResponse(sessionId);
  }

  @Override
  public void processOkClientResponse(EventMessage eventMessage, @NonNull String sessionId) {
    clientResponseService.processOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()));
  }

  @Override
  public void processNotOkClientResponse(EventMessage eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNotOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()), errorMessage);
  }
}
