package com.prosilion.superconductor.base.service.event.kind.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.KindTypeIF;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventKindTypeService implements EventKindTypeServiceIF {
  private final Map<Kind, Map<KindTypeIF, EventKindTypePluginIF>> eventKindTypePluginsMap;

  public EventKindTypeService(@NonNull List<EventKindTypePluginIF> eventKindTypePlugins) {
    this.eventKindTypePluginsMap = eventKindTypePlugins.stream()
        .collect(Collectors.groupingBy(
            EventKindTypePluginIF::getKind,
            Collectors.toMap(
                EventKindTypePluginIF::getKindType,
                Function.identity())));

    log.debug("Ctor (List<EventKindTypePluginIF>) loaded values:\n{}",
        eventKindTypePlugins.stream()
            .sorted(Comparator.comparing(
                eventKindTypePlugin ->
                    eventKindTypePlugin.getKind().getValue()))
            .map(eventKindTypePluginIF ->
                String.format("  Kind[%s]:%s -> KindType[%s]:%s -> %s",
                    eventKindTypePluginIF.getKind().getValue(),
                    eventKindTypePluginIF.getKind().getName().toUpperCase(),
                    eventKindTypePluginIF.getKindType().getKindDefinition().getValue(),
                    eventKindTypePluginIF.getKindType().getKindDefinition().getName().toUpperCase(),
                    eventKindTypePluginIF.getClass().getSimpleName()))
            .collect(Collectors.joining("\n")));
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    log.debug("processIncomingEvent() called with event:\n{}", event.createPrettyPrintJson());
    Kind kind = event.getKind();
    log.debug("processIncomingEvent() event.getKind(): [{}]", kind);

    Optional<EventKindTypePluginIF> optionalEventKindTypePluginIF = Optional
        .ofNullable(eventKindTypePluginsMap
            .get(kind)
            .get(getKindType(event)));

    EventKindTypePluginIF eventKindTypePluginIF = optionalEventKindTypePluginIF
        .orElseThrow(() -> new NostrException(
            String.format("eventKindTypePluginsMap does not contain matching entry for Kind [%s], KindType [%s]", event.getKind(), getKindType(event))));

    eventKindTypePluginIF.processIncomingEvent(event);
  }

  @Override
  public final List<KindTypeIF> getKindTypes() {
    return eventKindTypePluginsMap.values().stream()
        .map(Map::keySet)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @Override
  public final List<Kind> getKinds() {
    return List.copyOf(eventKindTypePluginsMap.keySet());
  }

  private KindTypeIF getKindType(EventIF event) {
    KindTypeIF kindTypeIF = getKindTypes().stream().filter(kindTypeIF2 ->
            isEquals(event, kindTypeIF2))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("No default KindType was specified"));
    return kindTypeIF;
  }

  private static boolean isEquals(EventIF event, KindTypeIF kindTypeIF2) {
    String kindTypeIF2Name = kindTypeIF2.getName();
    boolean equals = kindTypeIF2Name.equals(getExternalIdentifierIdentity(event));
    return equals;
  }

  private static String getExternalIdentifierIdentity(EventIF event) {
    ExternalIdentityTag externalIdentityTag = Filterable.getTypeSpecificTags(ExternalIdentityTag.class, event).stream()
        .findFirst().orElseThrow();
    String identity = externalIdentityTag.getIdentity();
    return identity;
  }
}
