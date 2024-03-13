package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.config.EventServiceFactory;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Log
public class EventMessageService<T extends EventMessage> implements MessageService<EventMessage> {

  private final EventService<EventMessage> eventService;
  private final T eventMessage;

  public EventMessageService(@NotNull T eventMessage) {
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    // TOOD: update subscription logic
    eventMessage.setSubscriptionId("SUB ID");
    var kind = ((GenericEvent) eventMessage.getEvent()).getKind();
    log.log(Level.INFO, "EVENT message type: {0}", eventMessage.getEvent());
    this.eventMessage = eventMessage;
    eventService = EventServiceFactory.createEventService(kind, eventMessage);
  }

  @Override
  public EventMessage processIncoming() {
    return NIP01.createEventMessage(eventService.processIncoming(), eventMessage.getSubscriptionId());
  }
}
