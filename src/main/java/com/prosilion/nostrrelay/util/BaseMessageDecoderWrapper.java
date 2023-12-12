package com.prosilion.nostrrelay.util;

import com.prosilion.nostrrelay.service.EventMessageGeneric;
import com.prosilion.nostrrelay.service.MessageGeneric;
import jakarta.websocket.Decoder;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.EventMessage;

public class BaseMessageDecoderWrapper implements Decoder.Text<MessageGeneric> {
  @Override
  public MessageGeneric decode(String s) {
    BaseMessage message = new BaseMessageDecoder(s).decode();
    switch (message.getCommand()) {
      case "EVENT" -> {
        return new EventMessageGeneric((EventMessage) message);
      }
      default -> throw new AssertionError("Unknown command " + message.getCommand());
    }
  }

  @Override
  public boolean willDecode(String s) {
    return (s != null);
  }
}