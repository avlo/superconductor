package com.prosilion.superconductor.service.message.req;

import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.message.MessageService;
import com.prosilion.superconductor.service.request.ReqService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public class ReqMessageServiceAuthDecorator<T extends ReqMessage> implements MessageService<T> {
  private final ReqMessageService<T> reqMessageService;
  private final AuthEntityService authEntityService;

  @Autowired
  public ReqMessageServiceAuthDecorator(
      ReqService<T, GenericEvent> reqService,
      AuthEntityService authEntityService,
      ClientResponseService clientResponseService) {
    this.reqMessageService = new ReqMessageService<>(reqService, clientResponseService);
    this.authEntityService = authEntityService;
  }

  @Override
  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) {
    log.info("AUTH REQ decoded, contents: {}", reqMessage);

    try {
      authEntityService.findAuthEntityBySessionId(sessionId).orElseThrow();
    } catch (NoSuchElementException e) {
      log.info("AUTHENTICATED REQ message failed session authentication");
      reqMessageService.processNoticeClientResponse(reqMessage, sessionId, String.format("restricted: session [%s] has not been authenticated", sessionId));
      return;
    }
    reqMessageService.processIncoming(reqMessage, sessionId);
  }

  @Override
  public String getCommand() {
    return reqMessageService.getCommand();
  }
}