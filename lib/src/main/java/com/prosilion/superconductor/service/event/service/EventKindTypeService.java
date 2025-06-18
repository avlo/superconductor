package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.Type;
import com.prosilion.nostr.event.AbstractBadgeAwardEventIF;
import com.prosilion.superconductor.service.event.service.plugin.AbstractEventTypePluginIF;
import com.prosilion.superconductor.service.event.service.plugin.EventTypePluginIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class EventKindTypeService<V extends Type, T extends AbstractBadgeAwardEventIF<V>> implements EventKindTypeServiceIF<V, T> {
  private final Map<Kind, Map<V, AbstractEventTypePluginIF<Kind, V, T>>> abstractEventTypePluginsMapMap;

  @Autowired
  public EventKindTypeService(List<EventTypePluginIF<V, T, Kind>> eventTypePlugins) {
    abstractEventTypePluginsMapMap = eventTypePlugins.stream()
        .filter(AbstractEventTypePluginIF.class::isInstance)
        .map(tuEventTypePluginIF -> (AbstractEventTypePluginIF<Kind, V, T>) tuEventTypePluginIF)
        .collect(Collectors.groupingBy(AbstractEventTypePluginIF::getKind, Collectors.toMap(
            AbstractEventTypePluginIF::getType, Function.identity())));
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    Map<V, AbstractEventTypePluginIF<Kind, V, T>> vAbstractEventTypePluginIFMap1 = Optional.ofNullable(
        abstractEventTypePluginsMapMap.get(event.getKind())).orElseThrow();
    AbstractEventTypePluginIF<Kind, V, T> tuvAbstractEventTypePluginIF = Optional.ofNullable(vAbstractEventTypePluginIFMap1.get(event.getType())).orElseThrow();
    tuvAbstractEventTypePluginIF.processIncomingEvent(event);
  }
}
