package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventKindService<T extends GenericEventKindIF> implements EventKindServiceIF<T> {
  private final Map<Kind, EventKindPluginIF<T>> eventKindPluginsMap;

  @Autowired
  public EventKindService(List<EventKindPluginIF<T>> eventKindPlugins) {
    this.eventKindPluginsMap = eventKindPlugins.stream().collect(
        Collectors.toMap(EventKindPluginIF::getKind, Function.identity()));
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    Optional.ofNullable(
            eventKindPluginsMap.get(
                event.getKind()))
        .orElse(
            eventKindPluginsMap.get(Kind.TEXT_NOTE)).processIncomingEvent(event); // everything else handled as TEXT_NOTE kind
  }
}
