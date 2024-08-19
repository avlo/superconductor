package com.prosilion.superconductor.service.okresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.api.factory.impl.NIP01Impl;
import org.springframework.web.socket.TextMessage;

@Setter
@Getter
public class CloseClientResponse {
  private final TextMessage closeResponseMessage;
  private final String sessionId;

  public CloseClientResponse(@NonNull String sessionId) throws JsonProcessingException {
    this.sessionId = sessionId;
    this.closeResponseMessage = new TextMessage(
        new NIP01Impl.CloseMessageFactory(sessionId).create().encode());
  }
}
