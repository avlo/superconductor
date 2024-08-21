package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.request.ReqService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public class ReqMessageServiceAuthDecorator<T extends ReqMessage> implements MessageService<T> {
  private final ReqMessageService<T> reqMessageService;
  private final AuthEntityService authEntityService;

  @Autowired
  public ReqMessageServiceAuthDecorator(ReqService<T, GenericEvent> reqService, AuthEntityService authEntityService) {
    this.reqMessageService = new ReqMessageService<>(reqService);
    this.authEntityService = authEntityService;
  }

  @Override
  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) {
    log.info("AUTH REQ decoded, contents: {}", reqMessage);
    authEntityService.findAuthEntityBySessionId(sessionId).orElseThrow();
    reqMessageService.processIncoming(reqMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return reqMessageService.getCommand();
  }
}