package com.prosilion.superconductor.base.service.event.kind.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.KindTypeIF;
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
  private final Map<Kind, Map<KindTypeIF, EventKindTypePluginIF<? extends BaseEvent>>> eventKindTypePluginsMap;

  public EventKindTypeService(@NonNull List<EventKindTypePluginIF<? extends BaseEvent>> eventKindTypePlugins) throws JsonProcessingException {
    this.eventKindTypePluginsMap = eventKindTypePlugins.stream()
        .collect(Collectors.groupingBy(
            EventKindTypePluginIF::getKind,
            Collectors.toMap(
                EventKindTypePluginIF::getKindType,
                Function.identity())));
    log.debug("loaded eventKindTypePluginsMap\n{}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(eventKindTypePluginsMap));
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    EventKindTypePluginIF<? extends BaseEvent> eventKindTypePluginIF = Optional
        .ofNullable(eventKindTypePluginsMap
            .get(event.getKind())
            .get(getKindType(event)))
        .orElseThrow(() -> new NostrException(
            String.format("eventKindTypePluginsMap does not contain matching entry for kind [%s], kindtype [%s]", event.getKind(), getKindType(event))));

//    eventKindTypePluginIF.processIncomingEvent(event);
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
