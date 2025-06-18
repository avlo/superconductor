package com.prosilion.superconductor.service.message.req.auth;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.service.message.req.ReqMessageServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AutoConfigReqMessageServiceNoAuthDecorator<T extends ReqMessage> implements AutoConfigReqMessageServiceIF<T> {
  private final ReqMessageServiceIF<T> reqMessageService;

  public AutoConfigReqMessageServiceNoAuthDecorator(@NonNull ReqMessageServiceIF<T> reqMessageService) {
    this.reqMessageService = reqMessageService;
  }

  @Override
  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) {
    log.debug("REQ decoded, contents: {}", reqMessage);
    reqMessageService.processIncoming(reqMessage, sessionId);
  }

  @Override
  public Command getCommand() {
    return Command.REQ;
  }
}
