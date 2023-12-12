package com.prosilion.nostrrelay.service;

import nostr.event.BaseMessage;
import nostr.event.message.EventMessage;

public class EventMessageCauldron implements MessageCauldron {
  private final EventMessage eventMessage;
  private final EventService eventService;

  public EventMessageCauldron(EventMessage eventMessage) {
    this.eventMessage = eventMessage;
    this.eventService = new EventServiceImpl(new EventMessageServiceImpl());
  }

  @Override
  public BaseMessage processIncoming() {
    return eventService.processIncoming(this);
  }

  @Override
  public EventMessage getMessage() {
    return eventMessage;
  }
}
