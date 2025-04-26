package com.prosilion.superconductor.service.message.req;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.request.ReqService;
import com.prosilion.superconductor.util.EmptyFiltersException;
import lombok.NonNull;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;

public class ReqMessageService<T extends ReqMessage> implements ReqMessageServiceBean<T> {

  private final ReqService<GenericEvent> reqService;
  private final ClientResponseService clientResponseService;

  public ReqMessageService(
      @NonNull ReqService<GenericEvent> reqService,
      @NonNull ClientResponseService clientResponseService) {
    this.reqService = reqService;
    this.clientResponseService = clientResponseService;
  }

  @Override
  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) {
    try {
      reqService.processIncoming(reqMessage, sessionId);
    } catch (EmptyFiltersException e) {
      clientResponseService.processNoticeClientResponse(reqMessage, sessionId, e.getMessage(), true);
    }
  }

  @Override
  public void processNoticeClientResponse(@NonNull T reqMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNoticeClientResponse(reqMessage, sessionId, errorMessage, false);
  }
}
