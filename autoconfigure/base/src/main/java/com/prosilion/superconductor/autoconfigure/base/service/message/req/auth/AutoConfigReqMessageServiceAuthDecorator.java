package com.prosilion.superconductor.autoconfigure.base.service.message.req.auth;

import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.base.service.event.auth.AuthPersistantIF;
import com.prosilion.superconductor.base.service.event.auth.AuthPersistantServiceIF;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AutoConfigReqMessageServiceAuthDecorator<T, U extends AuthPersistantIF> implements AutoConfigReqMessageServiceIF {
  private final ReqMessageServiceIF reqMessageService;
  private final AuthPersistantServiceIF<T, U> authPersistantServiceIF;

  public AutoConfigReqMessageServiceAuthDecorator(
      @NonNull ReqMessageServiceIF reqMessageService,
      @NonNull AuthPersistantServiceIF<T, U> authPersistantServiceIF) {
    this.reqMessageService = reqMessageService;
    this.authPersistantServiceIF = authPersistantServiceIF;
    log.debug("loaded bean (REQ AUTH)");
  }

  @Override
  public void processIncoming(@NonNull ReqMessage reqMessage, @NonNull String sessionId) {
    log.debug("processIncoming(reqMessage, sessionId) with ReqMessage filters:\n{}",
        reqMessage.getFiltersList().stream()
            .map(filters -> filters.toString(2))
            .collect(Collectors.joining(",\n")));
    try {
      authPersistantServiceIF.findAuthPersistantBySessionId(sessionId).orElseThrow();
    } catch (NoSuchElementException e) {
      log.debug("AUTHENTICATED REQ message failed session authentication");
      reqMessageService.processClientClosedResponse(sessionId, String.format("REQ sessionId [%s] has not been authenticated", sessionId));
      return;
    }
    reqMessageService.processIncoming(reqMessage, sessionId);
  }
}
