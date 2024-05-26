package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.AddSubscriberFiltersEvent;
import com.prosilion.superconductor.pubsub.RemoveSubscriberFilterEvent;
import com.prosilion.superconductor.pubsub.SubscriberNotifierEvent;
import lombok.Getter;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Service
public class EventNotifierEngine<T extends GenericEvent> {
  private final ApplicationEventPublisher publisher;


// TODO: below currently stores:
//
//      Map<SubscriberId, FiltersList Object>
//
//  should instead store:
//      Map<SubscriberId, FiltersList Id>
//
//  upon which a service lookup and filters list population occurs
//
  private final Map<Long, FiltersList> subscribersFiltersMap;



// TODO: below map currently stores:
//
//      Map<Kind, <Map<EventId, Event>>
//
//  should instead store two maps:
//      Map<Kind, ServiceType>  // static == fast
//  and then
//      InsertSortByKey: Map<EventId, Kind>  for fast lookup
//
//  upon which a service lookup and event object population occurs
  private final Map<Kind, Map<Long, T>> kindEventMap;


  @Autowired
  public EventNotifierEngine(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
    this.subscribersFiltersMap = new HashMap<>(new HashMap<>()); // use fast-hash map as/if necessary in the future
    this.kindEventMap = new EnumMap<>(Kind.class);
  }

  @EventListener
  public void nostrEventHandler(AddNostrEvent<T> addNostrEvent) {
    Map<Long, T> map = Optional.ofNullable(kindEventMap.get(addNostrEvent.kind())).orElse(new HashMap<>());
    map.putIfAbsent(addNostrEvent.id(), addNostrEvent.event());
    // TODO: if event is a replaceable event, update existing event
    kindEventMap.put(addNostrEvent.kind(), map);
    publisher.publishEvent(new SubscriberNotifierEvent<T>(subscribersFiltersMap, addNostrEvent));
  }

  @EventListener
  public void addSubscriberFiltersHandler(AddSubscriberFiltersEvent addSubscriber) {
    FiltersList filtersList = Optional.ofNullable(subscribersFiltersMap.get(addSubscriber.subscriberId())).orElse(addSubscriber.filtersList());

    Map<Long, FiltersList> subscriberFilterListMap = new HashMap<>(new HashMap<>());
    subscriberFilterListMap.put(addSubscriber.subscriberId(), filtersList);
    kindEventMap.forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            publisher.publishEvent(new SubscriberNotifierEvent<T>(subscriberFilterListMap, new AddNostrEvent<>(eventId, event, kind)))));

    subscribersFiltersMap.putIfAbsent(addSubscriber.subscriberId(), filtersList);
  }

  @EventListener
  public void removeSubscriberFilterHandler(RemoveSubscriberFilterEvent removeSubscriber) {
    subscribersFiltersMap.remove(removeSubscriber.subscriberId());
  }
}
