package com.prosilion.nostrrelay.service.message;

import com.prosilion.nostrrelay.service.request.ReqService;
import com.prosilion.nostrrelay.service.request.ReqServiceIF;
import jakarta.websocket.Session;
import lombok.extern.java.Log;
import nostr.event.message.ReqMessage;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Log
public class ReqMessageService<T extends ReqMessage> implements MessageService<ReqMessage> {
  private final ReqServiceIF<T> reqService;

  public ReqMessageService(@NotNull T reqMessage) {
    log.log(Level.INFO, "REQ fitlers: {0}", reqMessage.getFiltersList());
    reqService = new ReqService<>(reqMessage);
  }

  @Override
  public ReqMessage processIncoming(Session session) {
    return reqService.processIncoming(session);
  }
}