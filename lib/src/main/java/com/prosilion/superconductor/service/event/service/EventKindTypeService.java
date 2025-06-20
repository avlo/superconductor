package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindType;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.superconductor.service.event.service.plugin.AbstractEventKindTypePluginIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePluginIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class EventKindTypeService<T extends GenericEventKindTypeIF> implements EventKindTypeServiceIF<T> {
  private final Map<Kind, Map<KindType, AbstractEventKindTypePluginIF<T>>> eventKindTypePluginsMap;

  @Autowired
  public EventKindTypeService(List<EventKindTypePluginIF<T>> eventKindTypePlugins) {
    eventKindTypePluginsMap = eventKindTypePlugins.stream()
        .filter(AbstractEventKindTypePluginIF.class::isInstance)
        .map(eventKindTypePlugin -> (AbstractEventKindTypePluginIF<T>) eventKindTypePlugin)
        .collect(Collectors.groupingBy(AbstractEventKindTypePluginIF::getKind, Collectors.toMap(
            AbstractEventKindTypePluginIF::getKindType, Function.identity())));
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    Map<KindType, AbstractEventKindTypePluginIF<T>> value = Optional.ofNullable(
        eventKindTypePluginsMap.get(event.getKind())).orElseThrow();
    AbstractEventKindTypePluginIF<T> tuvAbstractEventTypePluginIF = Optional.ofNullable(value.get(event.getKindType())).orElseThrow();
    tuvAbstractEventTypePluginIF.processIncomingEvent(event);
  }
}
