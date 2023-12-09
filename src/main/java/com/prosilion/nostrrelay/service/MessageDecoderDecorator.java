package com.prosilion.nostrrelay.service;

import com.google.gson.Gson;
import jakarta.websocket.Decoder;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageDecoder;

public class MessageDecoderDecorator implements Decoder.Text<BaseMessage> {
  private static final Gson gson = new Gson();

  @Override
  public BaseMessage decode(String s) {
    BaseMessageDecoder baseMessageDecoder = new BaseMessageDecoder(s);
    return gson.fromJson(baseMessageDecoder.decode().getCommand(), BaseMessage.class);
  }

  @Override
  public boolean willDecode(String s) {
    return false;
  }
}