package com.prosilion.nostrrelay.util;

import jakarta.websocket.Encoder;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageEncoder;
import nostr.event.message.EventMessage;

public class MessageEncoder<T extends BaseMessage> implements Encoder.Text<T> {

  @Override
  public String encode(BaseMessage baseMessage) {
    return new BaseMessageEncoder(baseMessage, null).encode();
  }

  public String encode(EventMessage eventMessage) {
    return new BaseMessageEncoder(eventMessage, null).encode();
  }
}
