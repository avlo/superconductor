package com.prosilion.nostrrelay.service;

import nostr.event.BaseMessage;
import nostr.event.message.EventMessage;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {
  @Override
  public BaseMessage processIncoming(BaseMessage message) {
    switch (message.getCommand()) {
      case "EVENT" -> {
        if (message instanceof EventMessage msg) {
//          var subId = msg.getSubscriptionId();
          return msg;
        } else {
          throw new AssertionError("EventServiceImpl Assertion Error");
        }
      }
      default -> throw new AssertionError("Unknown command " + message.getCommand());
    }
  }
}
