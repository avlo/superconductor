package com.prosilion.superconductor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.websocket.Encoder;
import nostr.event.BaseMessage;

public class MessageEncoder implements Encoder.Text<BaseMessage> {

  @Override
  public String encode(BaseMessage baseMessage) {
      try {
          return baseMessage.encode();
      } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
      }
  }
}
