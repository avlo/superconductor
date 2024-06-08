package com.prosilion.superconductor.service.event;

import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
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
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    event.setNip(1);
    event.setKind(Kind.TEXT_NOTE.getValue());

    TextNoteEvent textNoteEvent = new TextNoteEvent(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    textNoteEvent.setId(event.getId());
    textNoteEvent.setCreatedAt(event.getCreatedAt());
    textNoteEvent.setSignature(event.getSignature());

    Long id = eventService.saveEventEntity(textNoteEvent);
    eventService.publishEvent(id, textNoteEvent);
  }

  @Override
  public Kind getKind() {
    return kind;
  }
}
