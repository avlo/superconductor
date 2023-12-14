package com.prosilion.nostrrelay.service;

import lombok.extern.java.Log;
import nostr.api.factory.impl.NIP01;
import nostr.base.IEvent;
import nostr.event.message.EventMessage;

import java.util.logging.Level;

@Log
public class TextNoteEventServiceImpl extends EventServiceImpl<EventMessage> {
  public TextNoteEventServiceImpl(EventMessage eventMessage) {
    super(eventMessage);
  }

  @Override
  public IEvent processIncoming() {
    log.log(Level.INFO, "processing incoming TEXT_NOTE_EVENT: [{0}]", getEvent());
    return new NIP01.TextNoteEventFactory("******************* SERVER CONFIRMS PROCESSED, DECORATOR PENDING *******************").create();
  }
}
