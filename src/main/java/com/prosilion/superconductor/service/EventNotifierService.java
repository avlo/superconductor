package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import lombok.Getter;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Service
public class EventNotifierService<T extends GenericEvent> {
  // TODO: below map currently stores:
//
//      Map<Kind(enum), <Map<EventId, Event>>
//
//  should instead store two maps:
//      Map<Kind, ServiceType>  // static == fast
//  and then
//      InsertSortByKey: Map<EventId, Kind>  for fast lookup
//
//  upon which a service lookup and event object population occurs
  private final Map<Kind, Map<Long, T>> kindEventMap;

  public EventNotifierService() {
    this.kindEventMap = new EnumMap<>(Kind.class);
  }

  public void updateEventMap(AddNostrEvent<T> addNostrEvent) {
    Map<Long, T> map = Optional.ofNullable(kindEventMap.get(addNostrEvent.kind())).orElse(new HashMap<>());
    map.putIfAbsent(addNostrEvent.id(), addNostrEvent.event());
    // TODO: if event is a replaceable event, update existing event
    kindEventMap.put(addNostrEvent.kind(), map);
  }

  public Map<Long, T> getEvents() {
    return kindEventMap.entrySet().stream().flatMap(entry ->
        entry.getValue().entrySet().stream()).collect(
        Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue
        ));
  }

  public List<AddNostrEvent<T>> getAddNostrEvents() {
    return kindEventMap.entrySet().stream().flatMap(kindMapEntry ->
            kindMapEntry.getValue().entrySet().stream().map(longTEntry ->
                new AddNostrEvent<T>(kindMapEntry.getKey(), longTEntry.getKey(), longTEntry.getValue())))
        .toList();
  }
}
