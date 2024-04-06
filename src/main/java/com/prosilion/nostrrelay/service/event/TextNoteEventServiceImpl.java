package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.service.event.EventServiceImpl;
import lombok.extern.java.Log;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;

import java.lang.reflect.InvocationTargetException;

@Log
public class TextNoteEventServiceImpl<T extends EventMessage> extends EventServiceImpl<T> {

  public TextNoteEventServiceImpl(T eventMessage) {
    super(eventMessage);
  }

  @Override
  public void processIncoming() throws InvocationTargetException, IllegalAccessException {
    GenericEvent event = (GenericEvent) getEventMessage().getEvent();
    event.setNip(1);
    event.setKind(Kind.TEXT_NOTE.getValue());
    super.saveEventEntity(event);
  }
}
