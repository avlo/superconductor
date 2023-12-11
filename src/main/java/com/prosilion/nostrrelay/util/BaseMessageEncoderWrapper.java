package com.prosilion.nostrrelay.util;

import jakarta.websocket.Encoder;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageEncoder;

public class BaseMessageEncoderWrapper implements Encoder.Text<BaseMessage> {

  @Override
  public String encode(BaseMessage baseMessage) {
    return new BaseMessageEncoder(baseMessage, null).encode();
  }
}
