package com.prosilion.superconductor.service.event.noop;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.event.EventMessageServiceIF;
import com.prosilion.superconductor.service.message.MessageService;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;

@Slf4j
public class EventMessageNoOpService<T extends EventMessage> implements EventMessageServiceIF<T> {
  public final String noOp;

  @Getter
  public final String command = "EVENT";
  private final ClientResponseService clientResponseService;

  public EventMessageNoOpService(ClientResponseService clientResponseService, String noOp) {
    this.clientResponseService = clientResponseService;
    this.noOp = noOp;
  }

  @Override
  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
    log.debug("processing incoming NOOP-EVENT: [{}]", eventMessage);
    clientResponseService.processNotOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()), noOp);
    clientResponseService.processCloseClientResponse(sessionId);
  }
}
