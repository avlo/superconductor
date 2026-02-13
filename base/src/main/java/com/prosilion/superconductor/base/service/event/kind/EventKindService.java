package com.prosilion.superconductor.base.service.event.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventKindPluginIF;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventKindService implements EventKindServiceIF {
  private static final String FORMATTER = "  %s -> %s";
  private final Map<Kind, EventKindPluginIF> eventKindPluginsMap;

  public <T extends BaseEvent> EventKindService(@NonNull List<EventKindPluginIF> eventKindPlugins) {
    this.eventKindPluginsMap = eventKindPlugins.stream().collect(
        Collectors.toMap(EventKindPluginIF::getKind, Function.identity()));

    log.debug("Ctor (List<EventKindPluginIF>) loaded values:\n{}",
        eventKindPlugins.stream()
            .sorted(Comparator.comparing(
                eventKindPluginIF ->
                    eventKindPluginIF.getKind().getValue()))
            .map(eventKindPluginIF ->
                String.format("  Kind[%s]:%s -> %s",
                    eventKindPluginIF.getKind().getValue(),
                    eventKindPluginIF.getKind().getName().toUpperCase(),
                    eventKindPluginIF.getClass().getSimpleName()))
            .collect(Collectors.joining("\n")));
  }

  @Override
  public void processIncomingEvent(EventIF event) {
    log.debug("processIncomingEvent() called with event:\n{}", event.createPrettyPrintJson());
    Kind kind = event.getKind();
    log.debug("processIncomingEvent() event.getKind(): [{}]", kind);

    EventKindPluginIF kindEventKindPluginIF =
        checkExistingEventKind(kind)
            .orElseGet(() ->
                useDefaultMapEntry(kind));

    if (kind.equals(Kind.DELETION)) {
      log.info("sanity check deletion plugin: {}", kindEventKindPluginIF);
      BaseEvent materialize = kindEventKindPluginIF.materialize(event);
      kindEventKindPluginIF.processIncomingEvent(materialize); // TODO: remove conditional after done testing
    } else {
      log.info("sanity check non-deletion plugin: {}", kindEventKindPluginIF);
      BaseEvent materialize = kindEventKindPluginIF.materialize(event);
      kindEventKindPluginIF.processIncomingEvent(materialize); // everything else handled as TEXT_NOTE kind
    }
  }

  private EventKindPluginIF useDefaultMapEntry(Kind kind) {
    EventKindPluginIF defaultEntry = eventKindPluginsMap.get(Kind.TEXT_NOTE);
    log.debug("processIncomingEvent() did not find map value for kind [{}], using default EventKindPluginIF [{}]",
        kind, defaultEntry);
    return defaultEntry;
  }

  private Optional<EventKindPluginIF> checkExistingEventKind(Kind kind) {
    EventKindPluginIF value = eventKindPluginsMap.get(kind);
    Optional<EventKindPluginIF> mapEntry = Optional.ofNullable(value);
    if (mapEntry.isPresent())
      log.debug("processIncomingEvent() found map value for kind [{}], using EventKindPluginIF [{}]",
          kind,
          value);
    return mapEntry;
  }

  @Override
  public final List<Kind> getKinds() {
    return List.copyOf(eventKindPluginsMap.keySet());
  }
}
