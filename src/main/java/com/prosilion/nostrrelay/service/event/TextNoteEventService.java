package com.prosilion.nostrrelay.service.event;

import lombok.extern.java.Log;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;

import java.lang.reflect.InvocationTargetException;

@Log
public class TextNoteEventService<T extends EventMessage> extends EventService<T, TextNoteEvent> {

  public TextNoteEventService(T eventMessage) {
    super(eventMessage);
  }

  @Override
  public void processIncoming() throws InvocationTargetException, IllegalAccessException {
    GenericEvent event = (GenericEvent) getEventMessage().getEvent();
    event.setNip(1);
    event.setKind(Kind.TEXT_NOTE.getValue());
    TextNoteEvent textNoteEvent = new TextNoteEvent(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    Long id = super.saveEventEntity(event);
    textNoteEvent.setId(event.getId());
    super.publishEvent(id, textNoteEvent);
  }
}
