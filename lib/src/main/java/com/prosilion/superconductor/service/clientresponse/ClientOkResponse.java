package com.prosilion.superconductor.service.clientresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NonNull;
import nostr.api.NIP20;
import nostr.event.impl.GenericEvent;
import org.springframework.web.socket.TextMessage;

@Getter
public class ClientOkResponse implements ClientResponse {
  private final TextMessage textMessage;
  private final String sessionId;
  private final boolean valid;

  public ClientOkResponse(@NonNull String sessionId, @NonNull GenericEvent event) throws JsonProcessingException {
    this(sessionId, event, true, "request successful");
  }

  public ClientOkResponse(@NonNull String sessionId, @NonNull GenericEvent event, boolean valid, @NonNull String message) throws JsonProcessingException {
    this.valid = valid;
    this.sessionId = sessionId;
    this.textMessage = new TextMessage(
        NIP20.createOkMessage(event, valid, message).encode());
  }
}
