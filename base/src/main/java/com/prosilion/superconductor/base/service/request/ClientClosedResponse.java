package com.prosilion.superconductor.base.service.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.message.ClosedMessage;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponse;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;

@Getter
public class ClientClosedResponse implements ClientResponse {
  private final TextMessage textMessage;
  private final String sessionId;
  private final boolean valid = false;

  public ClientClosedResponse(@NonNull String sessionId, @NonNull String reason) throws JsonProcessingException {
    this.sessionId = sessionId;
    this.textMessage = new TextMessage(new ClosedMessage(sessionId, reason).encode());
  }
}

