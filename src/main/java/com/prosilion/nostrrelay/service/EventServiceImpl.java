package com.prosilion.nostrrelay.service;

import lombok.extern.java.Log;
import nostr.event.BaseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Log
@Service
public class EventServiceImpl implements EventService<EventMessageCauldron> {
  final EventMessageService eventMessageService;

  @Autowired
  public EventServiceImpl(EventMessageService eventMessageService) {
    this.eventMessageService = eventMessageService;
  }

  @Override
  public BaseMessage processIncoming(EventMessageCauldron message) {
    log.log(Level.INFO, "EventServiceImpl processIncoming: {0}", message.getMessage().getCommand());
    return eventMessageService.getMessage(message.getMessage());
  }
}
