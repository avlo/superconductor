package com.prosilion.superconductor.base.service.request.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.BaseMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;

@Setter
@Getter
public class BroadcastMessageEvent<T extends BaseMessage> {
  private final TextMessage message;
  private final String sessionId;

  public BroadcastMessageEvent(@NonNull String sessionId, @NonNull T incoming) throws NostrException {
    this.sessionId = sessionId;
    try {
      this.message = new TextMessage((incoming).encode());
    } catch (JsonProcessingException e) {
      throw new NostrException("BroadcastMessageEvent encountered a JsonProcessingException", e);
    }
  }
}
