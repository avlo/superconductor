package com.prosilion.superconductor.service.event.type;

import lombok.NonNull;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventTypeService<T extends GenericEvent> implements EventTypeServiceIF<T> {
  private final Map<Kind, EventTypePlugin<T>> eventTypePluginsMap;


  @Autowired
  public EventTypeService(List<EventTypePlugin<T>> eventTypePlugins) {
    this.eventTypePluginsMap = eventTypePlugins.stream().collect(
        Collectors.toMap(EventTypePlugin::getKind, Function.identity()));
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    eventTypePluginsMap.get(Kind.valueOf(event.getKind())).processIncomingEvent(event);
  }
}
