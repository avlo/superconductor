package com.prosilion.nostrrelay.pubsub;

import lombok.Getter;
import lombok.Setter;
import nostr.event.message.CloseMessage;

@Setter
@Getter
public class CloseMessageEvent implements MessageEvent {
  @Getter
  private final String sessionId;
  private final CloseMessage closeMessage;

  public CloseMessageEvent(String sessionId, CloseMessage closeMessage) {
    this.sessionId = sessionId;
    this.closeMessage = closeMessage;
  }

  public String getMessage() {
    return closeMessage.toString();
  }
}
