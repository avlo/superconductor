package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.okresponse.ClientResponseService;
import com.prosilion.superconductor.service.request.EmptyFiltersException;
import com.prosilion.superconductor.service.request.ReqService;
import lombok.Getter;
import lombok.NonNull;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;

public class ReqMessageService<T extends ReqMessage> implements MessageService<T> {
  @Getter
  public final String command = "REQ";
  private final ReqService<T, GenericEvent> reqService;
  private final ClientResponseService clientResponseService;

  public ReqMessageService(
      @NonNull ReqService<T, GenericEvent> reqService,
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

  protected void processNoticeClientResponse(@NonNull T reqMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNoticeClientResponse(reqMessage, sessionId, errorMessage, false);
  }
}