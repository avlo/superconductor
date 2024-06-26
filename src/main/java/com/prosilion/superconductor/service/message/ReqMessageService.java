package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.request.ReqService;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReqMessageService<T extends ReqMessage> implements MessageService<T> {
  @Getter
  public final String command = "REQ";
  private final ReqService<T, GenericEvent> reqService;

  @Autowired
  public ReqMessageService(ReqService<T, GenericEvent> reqService) {
    this.reqService = reqService;
  }

  @Override
  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) {
    log.info("REQ decoded, contents: {}", reqMessage);
    reqService.processIncoming(reqMessage, sessionId);
  }
}