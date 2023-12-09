package com.prosilion.nostrrelay.service;

import com.google.gson.Gson;
import jakarta.websocket.Encoder;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageEncoder;

public class MessageEncoderDecorator implements Encoder.Text<BaseMessage> {
  private static final Gson gson = new Gson();

  @Override
  public String encode(BaseMessage baseMessage) {
    BaseMessageEncoder baseMessageEncoder = new BaseMessageEncoder(baseMessage, null);
    return gson.toJson(baseMessageEncoder.encode());
  }
}
