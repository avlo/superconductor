package com.prosilion.superconductor.autoconfigure.base.service.message.event.noop;

import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
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
  public void processOkClientResponse(@NonNull EventMessage eventMessage, @NonNull String sessionId) {
    clientResponseService.processOkClientResponse(sessionId, eventMessage);
  }

  @Override
  public void processNotOkClientResponse(@NonNull EventMessage eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNotOkClientResponse(sessionId, eventMessage, errorMessage);
  }
}
