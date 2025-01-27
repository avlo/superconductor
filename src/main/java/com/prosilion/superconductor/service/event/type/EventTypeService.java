package com.prosilion.superconductor.service.event.type;

import lombok.NonNull;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventTypeService<T extends GenericEvent> implements EventTypeServiceIF<T> {
  private final List<EventTypePlugin<T>> eventTypePlugins;

  @Autowired
  public EventTypeService(List<EventTypePlugin<T>> eventTypePlugins) {
    this.eventTypePlugins = eventTypePlugins;
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    eventTypePlugins.stream().filter(eventTypePlugin ->
            event.getKind().equals(eventTypePlugin.getKind().getValue()))
        .forEach(eventTypePlugin ->
            eventTypePlugin.processIncomingEvent(event));
  }
}
