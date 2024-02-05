package com.prosilion.nostrrelay.service;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.api.factory.impl.NIP01;
import nostr.base.IEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Log
@Getter
public class EventServiceImpl<T extends EventMessage> implements EventService<T> {
  private final T eventMessage;
  public EventServiceImpl(@NotNull T eventMessage) {
    this.eventMessage = eventMessage;
    log.log(Level.INFO, "EventService Constructed");
    log.log(Level.INFO, "EVENT message KIND: {0}", ((GenericEvent) eventMessage.getEvent()).getKind());
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    log.log(Level.INFO, "EVENT message JSON: {0}", new Gson().toJson(eventMessage.getEvent().toString()));
  }

  public IEvent processIncoming() {
    log.log(Level.INFO, "processing BASE EVENT...", eventMessage.getEvent().toString());
    return new NIP01.TextNoteEventFactory("******************* SERVER CONFIRMS PROCESSED, BASE *******************").create();
  }
}
