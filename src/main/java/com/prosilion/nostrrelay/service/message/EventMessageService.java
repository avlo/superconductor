package com.prosilion.nostrrelay.service.message;

import com.prosilion.nostrrelay.service.event.classified.ClassifiedEventServiceImpl;
import com.prosilion.nostrrelay.service.event.EventService;
import com.prosilion.nostrrelay.service.event.EventServiceImpl;
import com.prosilion.nostrrelay.service.event.textnote.TextNoteEventServiceImpl;
import jakarta.websocket.Session;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

@Log
public class EventMessageService<T extends EventMessage> implements MessageService<EventMessage> {
  private final EventService<T> eventService;
  private final T eventMessage;

  public EventMessageService(@NotNull T eventMessage) {
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    // TOOD: update subscription logic
    eventMessage.setSubscriptionId("SUB ID");
    var kind = ((GenericEvent) eventMessage.getEvent()).getKind();
    log.log(Level.INFO, "EVENT message type: {0}", eventMessage.getEvent());
    this.eventMessage = eventMessage;
    eventService = createEventService(Kind.valueOf(kind), eventMessage);
  }

  @Override
  public EventMessage processIncoming(Session session) throws InvocationTargetException, IllegalAccessException {
    return NIP01.createEventMessage(eventService.processIncoming(), eventMessage.getSubscriptionId());
  }

  private @NotNull EventService<T> createEventService(@NotNull Kind kind, T eventMessage) {
    switch (kind) {
      case SET_METADATA -> {
        log.log(Level.INFO, "SET_METADATA KIND decoded should match SET_METADATA -> [{0}]", kind.getName());
        return new EventServiceImpl<>(eventMessage);
      }
      case TEXT_NOTE -> {
        log.log(Level.INFO, "TEXT_NOTE KIND decoded should match TEXT_NOTE -> [{0}]", kind.getName());
        return new TextNoteEventServiceImpl<>(eventMessage);
      }
      case CLASSIFIED_LISTING -> {
        log.log(Level.INFO, "CLASSIFIED_LISTING KIND decoded should match CLASSIFIED_LISTING -> [{0}]", kind.getName());
        return new ClassifiedEventServiceImpl<>(eventMessage);
      }

      default -> throw new AssertionError("Unknown kind: " + kind.getName());
    }
  }
}