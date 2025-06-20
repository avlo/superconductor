package com.prosilion.superconductor.service.message.req;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.request.ReqServiceIF;
import com.prosilion.superconductor.util.EmptyFiltersException;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.message.ReqMessage;

public class ReqMessageService<T extends ReqMessage, U extends GenericEventKindIF> implements ReqMessageServiceIF<T> {

  private final ReqServiceIF<U> reqService;
  private final ClientResponseService clientResponseService;

  public ReqMessageService(
      @NonNull ReqServiceIF<U> reqService,
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
