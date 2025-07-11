package com.prosilion.superconductor.base.service.clientresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.message.CloseMessage;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;

@Getter
public class ClientCloseResponse implements ClientResponse {
  private final TextMessage textMessage;
  private final String sessionId;
  private final boolean valid = false;

  public ClientCloseResponse(@NonNull String sessionId) throws JsonProcessingException {
    this.sessionId = sessionId;
    this.textMessage = new TextMessage(new CloseMessage(sessionId).encode());
  }
}
