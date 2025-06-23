package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.superconductor.service.event.service.plugin.AbstractEventKindTypePlugin;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePluginIF;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class EventKindTypeService implements EventKindTypeServiceIF {
  private final Map<Kind, Map<KindTypeIF, AbstractEventKindTypePlugin>> eventKindTypePluginsMap;

  @Autowired
  public EventKindTypeService(List<EventKindTypePluginIF> eventKindTypePlugins) {
    eventKindTypePluginsMap = eventKindTypePlugins.stream()
        .filter(AbstractEventKindTypePlugin.class::isInstance)
        .map(eventKindTypePlugin -> (AbstractEventKindTypePlugin) eventKindTypePlugin)
        .collect(Collectors.groupingBy(AbstractEventKindTypePlugin::getKind, Collectors.toMap(
            AbstractEventKindTypePlugin::getKindType, Function.identity())));
  }

  @Override
  public void processIncomingKindTypeEvent(@NonNull GenericEventKindTypeIF event) {
    Map<KindTypeIF, AbstractEventKindTypePlugin> value = Optional.ofNullable(
        eventKindTypePluginsMap.get(event.getKind())).orElseThrow();
    List<KindTypeIF> definedKindTypes = event.getDefinedKindTypes();
    AbstractEventKindTypePlugin abstractEventKindTypePluginIF = definedKindTypes.stream().map(value::get).findFirst().orElseThrow();
    abstractEventKindTypePluginIF.processIncomingEvent(event);
  }

  @Override
  public KindTypeIF[] getKindTypesArray() {
    KindTypeIF[] array = eventKindTypePluginsMap.values().stream()
        .map(Map::keySet)
        .flatMap(Collection::stream)
        .toArray(KindTypeIF[]::new);
    return array;
  }

  @Override
  public List<KindTypeIF> getKindTypes() {
    List<KindTypeIF> list = eventKindTypePluginsMap.values().stream()
        .map(Map::keySet)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    return list;
  }

  @Override
  public void processIncomingEvent(GenericEventKindIF event) {
    throw new UnsupportedOperationException("shouldn't be here");
//    processIncomingKindTypeEvent(event);
  }

  @Override
  public Kind[] getKindArray() {
    return eventKindTypePluginsMap.keySet().toArray(Kind[]::new);
  }

  @Override
  public List<Kind> getKinds() {
    return new ArrayList<>(eventKindTypePluginsMap.keySet());
  }
}
