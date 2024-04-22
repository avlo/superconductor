package com.prosilion.nostrrelay.pubsub;

import lombok.Getter;
import lombok.Setter;
import nostr.event.BaseMessage;

@Setter
@Getter
public class BroadcastMessageEvent<T extends BaseMessage> {
  private final T message;
  private final String sessionId;

  public BroadcastMessageEvent(String sessionId, T message) {
    this.sessionId = sessionId;
    this.message = message;
  }
}
