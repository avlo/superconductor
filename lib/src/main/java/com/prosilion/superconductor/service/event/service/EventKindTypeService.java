package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindType;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePlugin;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePluginIF;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class EventKindTypeService implements EventKindTypeServiceIF<KindTypeIF> {
  private final Map<Kind, Map<KindTypeIF, EventKindTypePlugin>> eventKindTypePluginsMap;

  @Autowired
  public EventKindTypeService(List<EventKindTypePluginIF<KindTypeIF>> eventKindTypePlugins) {
    eventKindTypePluginsMap = eventKindTypePlugins.stream()
        .filter(EventKindTypePlugin.class::isInstance)
        .map(eventKindTypePlugin -> (EventKindTypePlugin) eventKindTypePlugin)
        .collect(Collectors.groupingBy(EventKindTypePlugin::getKind, Collectors.toMap(
            EventKindTypePlugin::getKindType, Function.identity())));
  }

  @Override
  public void processIncomingEvent(GenericEventKindIF event) {
    processIncomingEvent(
        new GenericEventKindType(
            event.getId(),
            event.getPublicKey(),
            event.getCreatedAt(),
            event.getKind(),
            event.getTags(),
            event.getContent(),
            event.getSignature(),
            getKindType(event)));
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindTypeIF event) {
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

  private KindTypeIF getKindType(GenericEventKindIF event) {
    return getKindTypes().stream().filter(kindTypeIF ->
        kindTypeIF.getName().equals(Filterable.getTypeSpecificTags(AddressTag.class, event)
            .stream()
            .findFirst()
            .map(AddressTag::getIdentifierTag).orElseThrow()
            .getUuid())).findFirst().orElseThrow();
  }
}
