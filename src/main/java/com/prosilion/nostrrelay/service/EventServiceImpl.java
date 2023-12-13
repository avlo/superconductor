package com.prosilion.nostrrelay.service;

import lombok.extern.java.Log;
import nostr.api.factory.impl.NIP01;
import nostr.base.IEvent;
import nostr.event.message.EventMessage;

import java.util.logging.Level;

@Log
public class EventServiceImpl<T extends EventMessageService<EventMessage>> implements EventService<T> {

  private final IEvent event;

  public EventServiceImpl(EventMessage eventMessage) {
    this.event = eventMessage.getEvent();
    log.log(Level.INFO, "EventService Constructed");
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    log.log(Level.INFO, "EVENT message JSON: {0}", event.toString());
  }

  public IEvent processIncoming() {
    log.log(Level.INFO, "processing incoming EVENT...", event.toString());
    return new NIP01.TextNoteEventFactory("******************* SERVER PROCESSED *******************").create();
  }
}
