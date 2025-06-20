package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
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
public class EventKindTypeService<KindType, T extends GenericEventKindTypeIF> implements EventKindTypeServiceIF<KindType, T> {
  private final Map<Kind, Map<KindType, AbstractEventTypePluginIF<Kind, KindType, T>>> abstractEventTypePluginsMapMap;

  @Autowired
  public EventKindTypeService(List<EventTypePluginIF<KindType, T, Kind>> eventTypePlugins) {
    abstractEventTypePluginsMapMap = eventTypePlugins.stream()
        .filter(AbstractEventTypePluginIF.class::isInstance)
        .map(tuEventTypePluginIF -> (AbstractEventTypePluginIF<Kind, KindType, T>) tuEventTypePluginIF)
        .collect(Collectors.groupingBy(AbstractEventTypePluginIF::getKind, Collectors.toMap(
            AbstractEventTypePluginIF::getKindType, Function.identity())));
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    Map<KindType, AbstractEventTypePluginIF<Kind, KindType, T>> value = Optional.ofNullable(
        abstractEventTypePluginsMapMap.get(event.getKind())).orElseThrow();
    AbstractEventTypePluginIF<Kind, KindType, T> tuvAbstractEventTypePluginIF = Optional.ofNullable(value.get(event.getKindType())).orElseThrow();
    tuvAbstractEventTypePluginIF.processIncomingEvent(event);
  }
}
