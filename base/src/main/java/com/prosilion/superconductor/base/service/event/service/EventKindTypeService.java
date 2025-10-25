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
    KindTypeIF kindType = getKindType(event);
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
            kindType));
  }

  private void processIncomingEvent(@NonNull GenericEventKindTypeIF event) {
    Map<KindTypeIF, EventKindTypePluginIF> kindTypeIFEventKindTypePluginIFMap = eventKindTypePluginsMap
        .get(event.getKind());
    EventKindTypePluginIF eventKindTypePluginIF = Optional.ofNullable(kindTypeIFEventKindTypePluginIFMap
        .get(event.getKindType())).orElse(defaultEventKindTypePluginIF);
    eventKindTypePluginIF
        .processIncomingEvent(event);
  }

  @Override
  public final List<KindTypeIF> getKindTypes() {
    List<KindTypeIF> collect = eventKindTypePluginsMap.values().stream()
        .map(Map::keySet)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    return collect;
  }

  @Override
  public final List<Kind> getKinds() {
    return List.copyOf(eventKindTypePluginsMap.keySet());
  }

  private KindTypeIF getKindType(EventIF event) {
    Optional<KindTypeIF> first = getKindTypes().stream().filter(kindTypeIF ->
            kindTypeIF.getName().equals(
                Filterable.getTypeSpecificTagsStream(AddressTag.class, event)
                    .findFirst()
                    .map(AddressTag::getIdentifierTag).orElseThrow()
                    .getUuid()))
        .findFirst();

    boolean present = first.isPresent();
    KindTypeIF kindTypeIF = first.orElse(getDefault().orElse(null));
    Optional<KindTypeIF> kindTypeIF1 = Optional.ofNullable(kindTypeIF);
    return kindTypeIF1.orElseThrow(() -> new NoSuchElementException("No default KindType was specified"));
  }

  private Optional<KindTypeIF> getDefault() {
    Optional<KindTypeIF> kindTypeIF = Optional
        .ofNullable(defaultEventKindTypePluginIF)
        .map(EventKindTypePluginIF::getKindType);
    kindTypeIF.ifPresent(kindTypeIF1 -> log.info("explicit KindType match not found, getting default KindType {}", kindTypeIF1));
    return kindTypeIF;
  }
}
