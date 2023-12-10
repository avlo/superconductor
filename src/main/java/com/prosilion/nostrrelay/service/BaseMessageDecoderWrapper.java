package com.prosilion.nostrrelay.service;

import jakarta.websocket.Decoder;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageDecoder;

public class BaseMessageDecoderWrapper implements Decoder.Text<BaseMessage> {
  @Override
  public BaseMessage decode(String s) {
    return new BaseMessageDecoder(s).decode();
  }

  @Override
  public boolean willDecode(String s) {
    return (s != null);
  }
}