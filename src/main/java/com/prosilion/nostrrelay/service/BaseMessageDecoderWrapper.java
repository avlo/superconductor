package com.prosilion.nostrrelay.service;

import jakarta.websocket.Decoder;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.EventMessage;

public class BaseMessageDecoderWrapper implements Decoder.Text<MessageCauldron<? extends BaseMessage>> {
  @Override
  public MessageCauldron<? extends BaseMessage> decode(String s) {
    BaseMessage message = new BaseMessageDecoder(s).decode();
    switch (message.getCommand()) {
      case "EVENT" -> {
        return new EventMessageCauldron((EventMessage)message);
      }
      default -> throw new AssertionError("Unknown command " + message.getCommand());
    }
  }

  @Override
  public boolean willDecode(String s) {
    return (s != null);
  }
}