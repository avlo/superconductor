package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.AddSubscriberFiltersEvent;
import com.prosilion.superconductor.pubsub.SubscriberNotifierEvent;
import lombok.Getter;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Service
public class EventNotifierEngine<T extends GenericEvent> {
  private final SubscriberNotifierService<T> subscriberNotifierService;
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
  public EventNotifierEngine(SubscriberNotifierService<T> subscriberNotifierService) {
    this.subscriberNotifierService = subscriberNotifierService;
    this.kindEventMap = new EnumMap<>(Kind.class);
  }

  @EventListener
  public void nostrEventHandler(AddNostrEvent<T> addNostrEvent) {
    Map<Long, T> map = Optional.ofNullable(kindEventMap.get(addNostrEvent.kind())).orElse(new HashMap<>());
    map.putIfAbsent(addNostrEvent.id(), addNostrEvent.event());
    // TODO: if event is a replaceable event, update existing event
    kindEventMap.put(addNostrEvent.kind(), map);
    subscriberNotifierService.notifySubscriber(addNostrEvent);
  }

  @EventListener
  public void addSubscriberFiltersHandler(AddSubscriberFiltersEvent subscriberFilters) {
    kindEventMap.forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            subscriberNotifierService.notifySubscriber(new SubscriberNotifierEvent<>(
                Map.of(subscriberFilters.subscriberId(), subscriberFilters.filtersList()),
                new AddNostrEvent<>(eventId, event, kind))
            )));
  }
}
