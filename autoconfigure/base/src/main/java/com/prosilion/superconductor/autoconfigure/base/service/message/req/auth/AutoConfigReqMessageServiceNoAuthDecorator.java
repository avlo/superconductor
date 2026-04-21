package com.prosilion.superconductor.autoconfigure.base.service.message.req.auth;

import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.ReqMessageServiceIF;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AutoConfigReqMessageServiceNoAuthDecorator implements AutoConfigReqMessageServiceIF {
  private final ReqMessageServiceIF reqMessageService;

  public AutoConfigReqMessageServiceNoAuthDecorator(@NonNull ReqMessageServiceIF reqMessageService) {
    this.reqMessageService = reqMessageService;
  }

  @Override
  public void processIncoming(@NonNull ReqMessage reqMessage, @NonNull String sessionId) {
    log.debug("processIncoming(reqMessage, sessionId):\n  sessionId:  [{}]\n  reqMesage.subscriptionId:  [{}]\n  with List<Filters>:\n  {}",
        sessionId,
        reqMessage.getSubscriptionId(),
        reqMessage.getFiltersList().stream()
            .map(filters -> filters.toString(2))
            .collect(Collectors.joining("],\n  [")));

    reqMessageService.processIncoming(reqMessage, sessionId);
  }
}
