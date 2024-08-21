package com.prosilion.superconductor.service.message;

import com.prosilion.superconductor.service.request.ReqService;
import lombok.Getter;
import lombok.NonNull;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;

public class ReqMessageService<T extends ReqMessage> implements MessageService<T> {
  @Getter
  public final String command = "REQ";
  private final ReqService<T, GenericEvent> reqService;

  public ReqMessageService(ReqService<T, GenericEvent> reqService) {
    this.reqService = reqService;
  }

  @Override
  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) {
    reqService.processIncoming(reqMessage, sessionId);
  }
}