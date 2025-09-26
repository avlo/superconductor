package com.prosilion.superconductor.autoconfigure.base.service.message.req.auth;

import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AutoConfigReqMessageServiceAuthDecorator implements AutoConfigReqMessageServiceIF {
  private final ReqMessageServiceIF reqMessageService;
  private final AuthPersistantServiceIF authPersistantServiceIF;

  public AutoConfigReqMessageServiceAuthDecorator(
      @NonNull ReqMessageServiceIF reqMessageService,
      @NonNull AuthPersistantServiceIF authPersistantServiceIF) {
    this.reqMessageService = reqMessageService;
    this.authPersistantServiceIF = authPersistantServiceIF;
  }

  @Override
  public void processIncoming(@NonNull ReqMessage reqMessage, @NonNull String sessionId) {
    log.debug("AUTH REQ decoded, contents: {}", reqMessage);
    try {
      authPersistantServiceIF.findAuthPersistantBySessionId(sessionId).orElseThrow();
    } catch (NoSuchElementException e) {
      log.debug("AUTHENTICATED REQ message failed session authentication");
      reqMessageService.processNoticeClientResponse(reqMessage, sessionId, String.format("restricted: REQ sessionId [%s] has not been authenticated", sessionId));
      return;
    }
    reqMessageService.processIncoming(reqMessage, sessionId);
  }
}
