package com.prosilion.nostrrelay.service.message;

import com.prosilion.nostrrelay.service.request.ReqService;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Log
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
    log.log(Level.INFO, "REQ decoded, contents: {0}", reqMessage.toString());
    reqService.processIncoming(reqMessage, sessionId);
  }
}