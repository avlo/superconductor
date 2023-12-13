package com.prosilion.nostrrelay.service;

import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.event.message.EventMessage;

import java.util.logging.Level;

@Log
public class EventMessageService<T extends EventMessage> implements MessageService<EventMessage> {
  private final EventServiceImpl<EventMessageService<EventMessage>> eventService;
  private final T eventMessage;

  public EventMessageService(T eventMessage) {
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    // TOOD: update subscription logic
    eventMessage.setSubscriptionId("SUB ID");
    log.log(Level.INFO, "EVENT message type: {0}", eventMessage.getEvent());
    this.eventMessage = eventMessage;
    this.eventService = new EventServiceImpl<>(eventMessage);
  }

  @Override
  public EventMessage processIncoming() {
    return NIP01.createEventMessage(eventService.processIncoming(), eventMessage.getSubscriptionId());
  }
}
