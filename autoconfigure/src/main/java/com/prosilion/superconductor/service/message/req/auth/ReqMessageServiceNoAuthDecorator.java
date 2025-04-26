package com.prosilion.superconductor.service.message.req.auth;

import com.prosilion.superconductor.service.message.req.ReqMessageServiceBean;
import com.prosilion.superconductor.service.message.req.ReqMessageServiceIF;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.ReqMessage;

@Slf4j
public class ReqMessageServiceNoAuthDecorator<T extends ReqMessage> implements ReqMessageServiceIF<T> {
  public final String command = "REQ";
  private final ReqMessageServiceBean<T> reqMessageService;

  public ReqMessageServiceNoAuthDecorator(@NonNull ReqMessageServiceBean<T> reqMessageService) {
    this.reqMessageService = reqMessageService;
  }

  @Override
  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) {
    log.debug("REQ decoded, contents: {}", reqMessage);
    reqMessageService.processIncoming(reqMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return command;
  }
}
