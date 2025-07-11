package com.prosilion.superconductor.service.message.req.auth;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.service.message.req.ReqMessageServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AutoConfigReqMessageServiceNoAuthDecorator implements AutoConfigReqMessageServiceIF<ReqMessage> {
  private final ReqMessageServiceIF reqMessageService;

  public AutoConfigReqMessageServiceNoAuthDecorator(@NonNull ReqMessageServiceIF reqMessageService) {
    this.reqMessageService = reqMessageService;
  }

  @Override
  public void processIncoming(@NonNull ReqMessage reqMessage, @NonNull String sessionId) {
    log.debug("REQ decoded, contents: {}", reqMessage);
    reqMessageService.processIncoming(reqMessage, sessionId);
  }

  @Override
  public Command getCommand() {
    return Command.REQ;
  }
}
