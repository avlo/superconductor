package com.prosilion.superconductor.service.okresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NonNull;
import nostr.api.factory.impl.NIP01Impl;
import org.springframework.web.socket.TextMessage;

@Getter
public class ClientCloseResponse {
  private final TextMessage textMessage;
  private final String sessionId;

  public ClientCloseResponse(@NonNull String sessionId) throws JsonProcessingException {
    this.sessionId = sessionId;
    this.textMessage = new TextMessage(
        new NIP01Impl.CloseMessageFactory(sessionId).create().encode());
  }
}
