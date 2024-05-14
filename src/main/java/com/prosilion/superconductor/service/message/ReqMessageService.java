package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.request.ReqService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
public class ReqMessageService<T extends ReqMessage> implements MessageService<T> {
  public final String command = "REQ";
  private final ReqService<T> reqService;

  @Autowired
  public ReqMessageService(ReqService<T> reqService) {
    this.reqService = reqService;
  }

  @Override
  public void processIncoming(T reqMessage, String sessionId) {
    log.info("REQ decoded, contents: {}", reqMessage.toString());
    reqService.processIncoming(reqMessage, sessionId);
  }
}