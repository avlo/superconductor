package com.prosilion.superconductor.service.request.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.enums.NostrException;
import com.prosilion.nostr.message.BaseMessage;
import lombok.Getter;
import org.springframework.lang.NonNull;
import lombok.Setter;
import org.springframework.web.socket.TextMessage;

@Setter
@Getter
public class BroadcastMessageEvent<T extends BaseMessage> {
  private final TextMessage message;
  private final String sessionId;

  public BroadcastMessageEvent(@NonNull String sessionId, @NonNull T incoming) throws JsonProcessingException, NostrException {
    this.sessionId = sessionId;
    this.message = new TextMessage((incoming).encode());
  }
}
