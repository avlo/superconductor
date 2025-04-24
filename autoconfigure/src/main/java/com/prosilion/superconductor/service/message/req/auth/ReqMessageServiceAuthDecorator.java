package com.prosilion.superconductor.service.message.req.auth;

import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.message.req.ReqMessageServiceBean;
import com.prosilion.superconductor.service.message.req.ReqMessageServiceIF;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.ReqMessage;

@Slf4j
public class ReqMessageServiceAuthDecorator<T extends ReqMessage> implements ReqMessageServiceIF<T> {
  public final String command = "REQ";
  private final ReqMessageServiceBean<T> reqMessageService;
  private final AuthEntityService authEntityService;

  public ReqMessageServiceAuthDecorator(
      @NonNull ReqMessageServiceBean<T> reqMessageService,
      @NonNull AuthEntityService authEntityService) {
    this.reqMessageService = reqMessageService;
    this.authEntityService = authEntityService;
  }

  @Override
  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) {
    log.debug("AUTH REQ decoded, contents: {}", reqMessage);

    try {
      authEntityService.findAuthEntityBySessionId(sessionId).orElseThrow();
    } catch (NoSuchElementException e) {
      log.debug("AUTHENTICATED REQ message failed session authentication");
      reqMessageService.processNoticeClientResponse(reqMessage, sessionId, String.format("restricted: session [%s] has not been authenticated", sessionId));
      return;
    }
    reqMessageService.processIncoming(reqMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return command;
  }
}
