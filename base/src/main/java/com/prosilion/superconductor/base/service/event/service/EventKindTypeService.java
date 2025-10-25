package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.KindTypeIF;
import java.util.Collection;
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
  private final EventKindTypePluginIF defaultEventKindTypePluginIF;

  public EventKindTypeService(@NonNull List<EventKindTypePluginIF> eventKindTypePlugins) {
    this(eventKindTypePlugins, null);
  }

  public EventKindTypeService(
      @NonNull List<EventKindTypePluginIF> eventKindTypePlugins,
      @NonNull EventKindTypePluginIF defaultEventKindTypePluginIF) {
    this.eventKindTypePluginsMap = eventKindTypePlugins.stream()
        .collect(Collectors.groupingBy(
            EventKindTypePluginIF::getKind,
            Collectors.toMap(
                EventKindTypePluginIF::getKindType,
                Function.identity())));
    this.defaultEventKindTypePluginIF = defaultEventKindTypePluginIF;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    processIncomingEvent(
        new GenericEventKindType(
            new GenericEventKind(
                event.getId(),
                event.getPublicKey(),
                event.getCreatedAt(),
                event.getKind(),
                event.getTags(),
                event.getContent(),
                event.getSignature()),
            getKindType(event)));
  }

  private void processIncomingEvent(@NonNull GenericEventKindTypeIF event) {
    eventKindTypePluginsMap
        .get(event.getKind())
        .get(event.getKindType())
        .processIncomingEvent(event);
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
    return getKindTypes().stream().filter(kindTypeIF ->
            kindTypeIF.getName().equals(Filterable.getTypeSpecificTagsStream(AddressTag.class, event)
                .findFirst()
                .map(AddressTag::getIdentifierTag).orElseThrow()
                .getUuid())).findFirst()
        .orElse(getDefault());
  }

  private KindTypeIF getDefault() {
    log.info("explicit KindType match not found, getting default KindType");
    return Optional.ofNullable(defaultEventKindTypePluginIF.getKindType())
        .orElseThrow(() -> new NoSuchElementException("No default KindType was specified"));
  }
}
