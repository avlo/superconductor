package com.prosilion.nostrrelay.service.event;

import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Log
@Getter
@Service
public class TextNoteEventService<T extends EventMessage> implements EventServiceIF<T> {
  public final Kind kind = Kind.TEXT_NOTE;
  private final EventService<TextNoteEvent> eventService;

  public TextNoteEventService(EventService<TextNoteEvent> eventService) {
    this.eventService = eventService;
  }

  @Override
  public void processIncoming(T eventMessage) {
    log.log(Level.INFO, "processing incoming TEXT_NOTE: [{0}]", eventMessage);
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    event.setNip(1);
    event.setKind(Kind.TEXT_NOTE.getValue());
    TextNoteEvent textNoteEvent = new TextNoteEvent(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    Long id = eventService.saveEventEntity(event);
    textNoteEvent.setId(event.getId());
    eventService.publishEvent(id, textNoteEvent);
  }
}
