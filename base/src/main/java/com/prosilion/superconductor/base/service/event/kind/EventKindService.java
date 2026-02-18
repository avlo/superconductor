package com.prosilion.superconductor.base.service.event.kind;

import com.prosilion.nostr.NostrException;
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
  private final Map<Kind, EventKindPluginIF> eventKindPluginsMap;
  private final String convenientKindMapList;

  public <T extends BaseEvent> EventKindService(@NonNull List<EventKindPluginIF> eventKindPlugins) {
    this.eventKindPluginsMap = eventKindPlugins.stream().collect(
        Collectors.toMap(EventKindPluginIF::getKind, Function.identity()));

    this.convenientKindMapList = eventKindPlugins.stream()
        .sorted(Comparator.comparing(
            eventKindPluginIF ->
                eventKindPluginIF.getKind().getValue()))
        .map(eventKindPluginIF ->
            String.format("  Kind[%s]:%s -> %s",
                eventKindPluginIF.getKind().getValue(),
                eventKindPluginIF.getKind().getName().toUpperCase(),
                eventKindPluginIF.getClass().getSimpleName()))
        .collect(Collectors.joining("\n"));

    log.debug("Ctor (List<EventKindPluginIF>) loaded values:\n  {}", convenientKindMapList);
  }

  @Override
  public void processIncomingEvent(EventIF event) {
    log.debug("processIncomingEvent() called with event:\n{}", event.createPrettyPrintJson());
    Kind kind = event.getKind();
    log.debug("processIncomingEvent() event.getKind(): [{}]", kind);

    EventKindPluginIF kindEventKindPluginIF = checkExistingEventKind(event);

    if (kind.equals(Kind.DELETION)) {
      log.info("sanity check deletion plugin: {}", kindEventKindPluginIF);
      kindEventKindPluginIF.processIncomingEvent(event); // TODO: remove conditional after done testing
    } else {
      log.info("sanity check non-deletion plugin: {}", kindEventKindPluginIF);
      kindEventKindPluginIF.processIncomingEvent(event); // everything else handled as TEXT_NOTE kind
    }
  }

  private EventKindPluginIF checkExistingEventKind(EventIF event) {
    Kind kind = event.getKind();
    EventKindPluginIF value = eventKindPluginsMap.get(kind);
    Optional<EventKindPluginIF> mapEntry = Optional.ofNullable(value);

    String kindFormat = String.format("[%s]:%s",
        event.getKind().getValue(),
        event.getKind().getName().toUpperCase());

    if (mapEntry.isEmpty()) {
      throw new NostrException(
          String.format("  event kind%s\nfrom event:\n%s\nnot present in eventKindPluginsMap:\n  %s",
              kindFormat,
              event.createPrettyPrintJson(),
              convenientKindMapList));
    }

    log.debug("found map value for kind{}, using EventKindPluginIF [{}]", kindFormat, value);
    return mapEntry.get();
  }

  @Override
  public final List<Kind> getKinds() {
    return List.copyOf(eventKindPluginsMap.keySet());
  }
}
