package com.prosilion.superconductor.service.event.noop;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.event.EventMessageServiceBean;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;

@Slf4j
public class EventMessageNoOpService<T extends EventMessage> implements EventMessageServiceBean<T> {
  public final String noOp;

  private final ClientResponseService clientResponseService;

  public EventMessageNoOpService(@NonNull ClientResponseService clientResponseService, @NonNull String noOp) {
    this.clientResponseService = clientResponseService;
    this.noOp = noOp;
  }

  @Override
  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    log.debug("processing incoming NOOP-EVENT: [{}]", eventMessage);
    processNotOkClientResponse((T) new EventMessage(eventMessage.getEvent()), sessionId, noOp);
    clientResponseService.processCloseClientResponse(sessionId);
  }

  @Override
  public void processOkClientResponse(T eventMessage, @NonNull String sessionId) {
    clientResponseService.processOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()));
  }

  @Override
  public void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNotOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()), errorMessage);
  }
}
