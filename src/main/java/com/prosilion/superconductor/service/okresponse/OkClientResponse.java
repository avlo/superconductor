package com.prosilion.superconductor.service.okresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import nostr.api.factory.impl.NIP20Impl;
import nostr.event.impl.GenericEvent;
import org.springframework.web.socket.TextMessage;

@Setter
@Getter
public class OkClientResponse {
  private final TextMessage okResponseMessage;
  private final String sessionId;

  public OkClientResponse(String sessionId, GenericEvent event) throws JsonProcessingException {
    this.sessionId = sessionId;
    this.okResponseMessage = new TextMessage(
        new NIP20Impl.OkMessageFactory(event, true, "").create().encode());
  }
}
