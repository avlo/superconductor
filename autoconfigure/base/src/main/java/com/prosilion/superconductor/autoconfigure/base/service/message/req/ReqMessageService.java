package com.prosilion.superconductor.autoconfigure.base.service.message.req;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.request.ReqServiceIF;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;

public class ReqMessageService implements ReqMessageServiceIF {
  public static final String AUTH_REQUIRED = "auth-required: ";
  
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
      clientResponseService.processClientNoticeResponse(reqMessage, sessionId, e.getMessage(), true);
    }
  }

  @Override
  public void processClientClosedResponse(@NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processClientClosedResponse(sessionId, Strings.concat(AUTH_REQUIRED, errorMessage));
  }

  @Override
  public void processClientNoticeResponse(@NonNull ReqMessage reqMessage, @NonNull String sessionId, @NonNull String errorMessage) {
    clientResponseService.processClientNoticeResponse(reqMessage, sessionId, errorMessage, false);
  }
}
