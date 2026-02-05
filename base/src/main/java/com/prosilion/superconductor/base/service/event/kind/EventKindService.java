package com.prosilion.superconductor.base.service.event.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventKindService implements EventKindServiceIF {
  private final Map<Kind, EventKindPluginIF<? extends BaseEvent>> eventKindPluginsMap;

  public <T extends BaseEvent> EventKindService(@NonNull List<EventKindPluginIF<? extends T>> eventKindPlugins) {
    this.eventKindPluginsMap = eventKindPlugins.stream().collect(
        Collectors.toMap(EventKindPluginIF::getKind, Function.identity()));
    log.info("eventKindPluginsMap: {}", eventKindPluginsMap);
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(EventIF event) {
    log.debug("{} processIncomingEvent() called with event {}", getClass().getSimpleName(), event.createPrettyPrintJson());
    Kind kind = event.getKind();
    log.debug("{} processIncomingEvent() event.getKind() [{}]", getClass().getSimpleName(), kind);
    EventKindPluginIF<T> kindEventKindPluginIF =
        (EventKindPluginIF<T>) checkExistingEventKind(kind)
            .orElseGet(() ->
                useDefaultMapEntry(kind));

    if (kind.equals(Kind.DELETION)) {
      log.info("plugin: {}", kindEventKindPluginIF);
//      kindEventKindPluginIF.processIncomingEvent(event); // everything else handled as TEXT_NOTE kind
    } else {
      log.info("plugin: {}", kindEventKindPluginIF);
//      kindEventKindPluginIF.processIncomingEvent(event); // everything else handled as TEXT_NOTE kind
    }
  }

  private EventKindPluginIF<? extends BaseEvent> useDefaultMapEntry(Kind kind) {
    EventKindPluginIF<? extends BaseEvent> defaultEntry = eventKindPluginsMap.get(Kind.TEXT_NOTE);
    log.debug("{} processIncomingEvent() did not find map value for kind [{}], using default EventKindPluginIF [{}]",
        getClass().getSimpleName(),
        kind, defaultEntry);
    return defaultEntry;
  }

  private Optional<EventKindPluginIF<? extends BaseEvent>> checkExistingEventKind(Kind kind) {
    EventKindPluginIF<? extends BaseEvent> value = eventKindPluginsMap.get(kind);
    Optional<EventKindPluginIF<? extends BaseEvent>> mapEntry = Optional.ofNullable(value);
    if (mapEntry.isPresent())
      log.debug("{} processIncomingEvent() found map value for kind [{}], using EventKindPluginIF [{}]",
          getClass().getSimpleName(),
          kind,
          value);
    return mapEntry;
  }

  @Override
  public final List<Kind> getKinds() {
    return List.copyOf(eventKindPluginsMap.keySet());
  }
}
