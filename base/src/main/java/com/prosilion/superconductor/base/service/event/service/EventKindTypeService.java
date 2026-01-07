package com.prosilion.superconductor.base.service.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.KindTypeIF;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventKindTypeService implements EventKindTypeServiceIF {
  private final Map<Kind, Map<KindTypeIF, EventKindTypePluginIF>> eventKindTypePluginsMap;

  public EventKindTypeService(@NonNull List<EventKindTypePluginIF> eventKindTypePlugins) throws JsonProcessingException {
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
            getKindType(event)),
        (GenericEventRecord) event);
  }

  private void processIncomingEvent(@NonNull GenericEventKindTypeIF event, GenericEventRecord genericEventRecord) {
    EventKindTypePluginIF eventKindTypePluginIF = Optional
        .ofNullable(eventKindTypePluginsMap
            .get(event.getKind())
            .get(event.getKindType()))
        .orElseThrow();
    
    eventKindTypePluginIF.processIncomingEvent(genericEventRecord);
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
    boolean equals = kindTypeIF2Name.equals(getExternalIdentifierIdenitity(event));
    return equals;
  }

  private static String getExternalIdentifierIdenitity(EventIF event) {
    ExternalIdentityTag externalIdentityTag = Filterable.getTypeSpecificTags(ExternalIdentityTag.class, event).stream()
        .findFirst().orElseThrow();
    String identity = externalIdentityTag.getIdentity();
    return identity;
  }
}
