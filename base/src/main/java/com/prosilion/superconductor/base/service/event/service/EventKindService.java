package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
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
  public void processIncomingEvent(@NonNull EventIF event) {
    Kind kind = event.getKind();
    EventKindPluginIF<Kind> value = eventKindPluginsMap.get(kind);
    EventKindPluginIF<Kind> kindEventKindPluginIF = Optional.ofNullable(
            value)
        .orElse(
            eventKindPluginsMap.get(Kind.TEXT_NOTE));

    if (kind.equals(Kind.DELETION)) {
      log.info("plugin: {}", kindEventKindPluginIF);
      kindEventKindPluginIF.processIncomingEvent(event); // everything else handled as TEXT_NOTE kind
    } else {
      log.info("plugin: {}", kindEventKindPluginIF);
      kindEventKindPluginIF.processIncomingEvent(event); // everything else handled as TEXT_NOTE kind
    }
  }

  @Override
  public final List<Kind> getKinds() {
    return List.copyOf(eventKindPluginsMap.keySet());
  }
}
