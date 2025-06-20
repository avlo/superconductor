package com.prosilion.superconductor.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
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
public class EventKindTypeService implements EventKindTypeServiceIF {
  private final Map<Kind, Map<KindTypeIF, AbstractEventKindTypePluginIF>> eventKindTypePluginsMap;

  @Autowired
  public EventKindTypeService(List<EventKindTypePluginIF> eventKindTypePlugins) {
    eventKindTypePluginsMap = eventKindTypePlugins.stream()
        .filter(AbstractEventKindTypePluginIF.class::isInstance)
        .map(eventKindTypePlugin -> (AbstractEventKindTypePluginIF) eventKindTypePlugin)
        .collect(Collectors.groupingBy(AbstractEventKindTypePluginIF::getKind, Collectors.toMap(
            AbstractEventKindTypePluginIF::getKindType, Function.identity())));
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindTypeIF event) {
    Map<KindTypeIF, AbstractEventKindTypePluginIF> value = Optional.ofNullable(
        eventKindTypePluginsMap.get(event.getKind())).orElseThrow();
    AbstractEventKindTypePluginIF tuvAbstractEventTypePluginIF = Optional.ofNullable(value.get(event.getKindType())).orElseThrow();
    tuvAbstractEventTypePluginIF.processIncomingEvent(event);
  }
}
