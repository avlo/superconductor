package com.prosilion.nostrrelay.service;

import lombok.Getter;
import lombok.extern.java.Log;
import nostr.api.factory.impl.NIP01;
import nostr.base.IEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;

import java.util.logging.Level;

@Log
@Getter
public class EventServiceImpl<T extends EventMessage> implements EventService<T> {

  private final IEvent event;

  public EventServiceImpl(EventMessage eventMessage) {
    this.event = eventMessage.getEvent();
    log.log(Level.INFO, "EventService Constructed");
    log.log(Level.INFO, "EVENT message KIND: {0}", ((GenericEvent) eventMessage.getEvent()).getKind());
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    log.log(Level.INFO, "EVENT message JSON: {0}", event.toString());
  }

  public IEvent processIncoming() {
    log.log(Level.INFO, "processing BASE EVENT...", event.toString());
    return new NIP01.TextNoteEventFactory("******************* SERVER CONFIRMS PROCESSED, BASE *******************").create();
  }
}
