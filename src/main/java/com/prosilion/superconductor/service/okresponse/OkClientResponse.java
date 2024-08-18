package com.prosilion.superconductor.service.okresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.api.factory.impl.NIP20Impl;
import nostr.event.impl.GenericEvent;
import org.springframework.web.socket.TextMessage;

@Setter
@Getter
public class OkClientResponse {
  private final TextMessage okResponseMessage;
  private final String sessionId;
  private final boolean valid;

  public OkClientResponse(@NonNull String sessionId, @NonNull GenericEvent event) throws JsonProcessingException {
    this(sessionId, event, true, "request successful");
  }

  public OkClientResponse(@NonNull String sessionId, @NonNull GenericEvent event, boolean valid, @NonNull String message) throws JsonProcessingException {
    this.valid = valid;
    this.sessionId = sessionId;
    this.okResponseMessage = new TextMessage(
        new NIP20Impl.OkMessageFactory(event, valid, message).create().encode());
  }
}
