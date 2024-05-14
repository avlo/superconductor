package com.prosilion.superconductor.pubsub;

import com.prosilion.superconductor.util.MessageEncoder;
import lombok.Getter;
import lombok.Setter;
import nostr.event.BaseMessage;
import org.springframework.web.socket.TextMessage;

@Setter
@Getter
public class BroadcastMessageEvent<T extends BaseMessage> {
  private final TextMessage message;
  private final String sessionId;

  public BroadcastMessageEvent(String sessionId, T incoming) {
    this.sessionId = sessionId;
    this.message = new TextMessage(new MessageEncoder().encode(incoming));
  }
}
