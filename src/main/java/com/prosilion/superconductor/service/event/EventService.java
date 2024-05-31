package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.NotifierService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Getter
@Service
public class EventService<T extends GenericEvent> {
  private final EventEntityService eventEntityService;
  private final NotifierService<T> notifierService;

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

  @Autowired
  public EventService(EventEntityService eventEntityService, NotifierService<T> notifierService) {
    this.eventEntityService = eventEntityService;
    this.notifierService = notifierService;
    this.kindEventMap = new EnumMap<>(Kind.class);
  }

  public void updateEventMap(AddNostrEvent<T> addNostrEvent) {
    Map<Long, T> map = Optional.ofNullable(kindEventMap.get(addNostrEvent.kind())).orElse(new HashMap<>());
    map.putIfAbsent(addNostrEvent.id(), addNostrEvent.event());
    // TODO: if event is a replaceable event, update existing event
    kindEventMap.put(addNostrEvent.kind(), map);
  }

  /**
   * public Map<Long, T> getEvents() {
   * return kindEventMap.entrySet().stream().flatMap(entry ->
   * entry.getValue().entrySet().stream()).collect(
   * Collectors.toMap(
   * Map.Entry::getKey,
   * Map.Entry::getValue
   * ));
   * }
   * <p>
   * public List<AddNostrEvent<T>> getAddNostrEvents() {
   * return kindEventMap.entrySet().stream().flatMap(kindMapEntry ->
   * kindMapEntry.getValue().entrySet().stream().map(longTEntry ->
   * new AddNostrEvent<T>(kindMapEntry.getKey(), longTEntry.getKey(), longTEntry.getValue())))
   * .toList();
   * }
   */

  protected Long saveEventEntity(T event) {
    return eventEntityService.saveEventEntity(event);
  }

  protected void publishEvent(Long id, T event) {
    AddNostrEvent<T> addNostrEvent = new AddNostrEvent<>(Kind.valueOf(event.getKind()), id, event);
    updateEventMap(addNostrEvent);
    notifierService.nostrEventHandler(addNostrEvent);
  }

  public void subscriptionEventHandler(Long subscriberId) {
    kindEventMap.forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            notifierService.subscriptionEventHandler(subscriberId, new AddNostrEvent<>(kind, eventId, event))));
  }
}