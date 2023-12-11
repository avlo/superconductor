package com.prosilion.nostrrelay.service;

import nostr.event.message.EventMessage;

public class EventMessageCauldron implements MessageCauldron<EventMessage> {
  private final EventMessage eventMessage;

  public EventMessageCauldron(EventMessage eventMessage) {
    this.eventMessage = eventMessage;
  }

  @Override
  public EventMessage getMessage() {
    return eventMessage;
  }
}
