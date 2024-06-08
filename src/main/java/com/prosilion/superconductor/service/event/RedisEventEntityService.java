package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class RedisEventEntityService<T extends GenericEvent> {

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

  public RedisEventEntityService() {
    this.kindEventMap = new EnumMap<>(Kind.class);
  }

  public Map<Kind, Map<Long, T>> getGottaProperlyDAOImplThisKindEventMap() {
    return kindEventMap;
  }

  public void updateEventMap(AddNostrEvent<T> addNostrEvent) {
    Map<Long, T> map = Optional.ofNullable(kindEventMap.get(addNostrEvent.kind())).orElse(new HashMap<>());
    map.putIfAbsent(addNostrEvent.id(), addNostrEvent.event());
    // TODO: if event is a replaceable event, update existing event
    kindEventMap.put(addNostrEvent.kind(), map);
  }
}