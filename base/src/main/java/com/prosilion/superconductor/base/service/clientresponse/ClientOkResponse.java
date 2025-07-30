package com.prosilion.superconductor.base.service.clientresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.message.OkMessage;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;

@Getter
public class ClientOkResponse implements ClientResponse {
  private final TextMessage textMessage;
  private final String sessionId;
  private final boolean valid;

  public ClientOkResponse(@NonNull String sessionId, @NonNull EventIF event) throws JsonProcessingException {
    this(sessionId, event, true, "request successful");
  }

  public ClientOkResponse(@NonNull String sessionId, @NonNull EventIF event, boolean valid, @NonNull String message) throws JsonProcessingException {
    this.valid = valid;
    this.sessionId = sessionId;
    this.textMessage = new TextMessage(new OkMessage(event.getId(), valid, message).encode());
  }
}
