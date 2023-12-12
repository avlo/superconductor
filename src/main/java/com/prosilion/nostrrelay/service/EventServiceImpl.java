package com.prosilion.nostrrelay.service;

import lombok.extern.java.Log;
import nostr.event.BaseMessage;

import java.util.logging.Level;

@Log
public class EventServiceImpl implements EventService<EventMessageCauldron> {
  final MessageService eventMessageService;

  public EventServiceImpl(EventMessageServiceImpl eventMessageService) {
    this.eventMessageService = eventMessageService;
  }

  @Override
  public BaseMessage processIncoming(EventMessageCauldron message) {
    log.log(Level.INFO, "EventServiceImpl processIncoming: {0}", message.getMessage().getCommand());
    return eventMessageService.getMessage(message.getMessage());
  }
}
