package com.prosilion.superconductor.service.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
public class TextNoteEventService<T extends EventMessage> implements EventServiceIF<T> {
  public final Kind kind = Kind.TEXT_NOTE;
  private final EventService<TextNoteEvent> eventService;

  public TextNoteEventService(EventService<TextNoteEvent> eventService) {
    this.eventService = eventService;
  }

  @Override
  @Async
  public void processIncoming(T eventMessage) {
    log.info("processing incoming TEXT_NOTE: [{}]", eventMessage);
    TextNoteEvent event = (TextNoteEvent) eventMessage.getEvent();
    event.setNip(1);
    event.setKind(Kind.TEXT_NOTE.getValue());
    Long id = eventService.saveEventEntity(event);

    TextNoteEvent textNoteEvent = new TextNoteEvent(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    textNoteEvent.setId(event.getId());
    textNoteEvent.setCreatedAt(event.getCreatedAt());
    eventService.publishEvent(id, textNoteEvent);
  }
}
