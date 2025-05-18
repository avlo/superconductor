package com.prosilion.superconductor.service.clientresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NonNull;
import nostr.api.NIP01;
import org.springframework.web.socket.TextMessage;

@Getter
public class ClientCloseResponse implements ClientResponse {
  private final TextMessage textMessage;
  private final String sessionId;
  private final boolean valid = false;

  public ClientCloseResponse(@NonNull String sessionId) throws JsonProcessingException {
    this.sessionId = sessionId;
    this.textMessage = new TextMessage(
        NIP01.createCloseMessage(sessionId).encode());
  }
}
