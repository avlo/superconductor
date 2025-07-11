package com.prosilion.superconductor.service.message.req;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.request.ReqServiceIF;
import org.springframework.lang.NonNull;

public class ReqMessageService implements ReqMessageServiceIF {

  private final ReqServiceIF reqService;
  private final ClientResponseService clientResponseService;

  public ReqMessageService(
      @NonNull ReqServiceIF reqService,
      @NonNull ClientResponseService clientResponseService) {
    this.reqService = reqService;
    this.clientResponseService = clientResponseService;
  }

  @Override
  public void processIncoming(@NonNull ReqMessage reqMessage, @NonNull String sessionId) {
    try {
      reqService.processIncoming(reqMessage, sessionId);
    } catch (NostrException e) {
      clientResponseService.processNoticeClientResponse(reqMessage, sessionId, e.getMessage(), true);
    }
  }

  @Override
  public void processNoticeClientResponse(@NonNull ReqMessage reqMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processNoticeClientResponse(reqMessage, sessionId, errorMessage, false);
  }
}
