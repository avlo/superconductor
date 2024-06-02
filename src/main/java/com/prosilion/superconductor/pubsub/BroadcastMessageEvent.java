package com.prosilion.superconductor.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import nostr.event.BaseMessage;
import org.springframework.web.socket.TextMessage;

@Setter
@Getter
public class BroadcastMessageEvent<T extends BaseMessage> {
  private final TextMessage message;
  private final String sessionId;

  public BroadcastMessageEvent(String sessionId, T incoming) throws JsonProcessingException {
    this.sessionId = sessionId;
    this.message = new TextMessage((incoming).encode());
  }
}
