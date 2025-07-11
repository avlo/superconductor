package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventKindService implements EventKindServiceIF {
  private final Map<Kind, EventKindPluginIF<Kind>> eventKindPluginsMap;

  public EventKindService(List<EventKindPluginIF<Kind>> eventKindPlugins) {
    this.eventKindPluginsMap = eventKindPlugins.stream().collect(
        Collectors.toMap(EventKindPluginIF::getKind, Function.identity()));
    log.info("eventKindPluginsMap: {}", eventKindPluginsMap);
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
    Optional.ofNullable(
            eventKindPluginsMap.get(
                event.getKind()))
        .orElse(
            eventKindPluginsMap.get(Kind.TEXT_NOTE)).processIncomingEvent(event); // everything else handled as TEXT_NOTE kind
  }

  @Override
  public final List<Kind> getKinds() {
    return List.copyOf(eventKindPluginsMap.keySet());
  }
}
