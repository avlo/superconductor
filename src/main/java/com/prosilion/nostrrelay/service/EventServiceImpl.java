package com.prosilion.nostrrelay.service;

import lombok.extern.java.Log;
import nostr.event.BaseMessage;
import nostr.event.message.EventMessage;

import java.util.logging.Level;

@Log
public class EventServiceImpl implements EventService {
  @Override
  public BaseMessage processIncoming(BaseMessage message) {
    log.log(Level.INFO, "EventServiceImpl processIncoming: {0}", message.getCommand());
    switch (message.getCommand()) {
      case "EVENT" -> {
        if (message instanceof EventMessage msg) {
          log.log(Level.INFO, "EventServiceImpl instanceof EVENT: {0}", msg.getEvent());
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
