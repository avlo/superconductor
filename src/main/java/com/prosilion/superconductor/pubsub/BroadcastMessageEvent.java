package com.prosilion.superconductor.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseMessage;
import org.springframework.web.socket.TextMessage;

@Setter
@Getter
public class BroadcastMessageEvent<T extends BaseMessage> {
  private final TextMessage message;
  private final String sessionId;

  public BroadcastMessageEvent(@NonNull String sessionId, @NonNull T incoming) throws JsonProcessingException {
    this.sessionId = sessionId;
    this.message = new TextMessage((incoming).encode());
  }
}
