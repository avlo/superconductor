package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.AddSubscriberFiltersEvent;
import com.prosilion.nostrrelay.pubsub.RemoveSubscriberFilterEvent;
import com.prosilion.nostrrelay.pubsub.SubscriberNotifierEvent;
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
  private final Map<Long, FiltersList> subscribersFiltersMap;
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
    // TODO: below currently checks for existing subscriber filters, but does not yet update those existing
//      filters with incoming filters.  the latter is a TODO
    FiltersList filtersList = Optional.ofNullable(subscribersFiltersMap.get(addSubscriber.subscriberId())).orElse(addSubscriber.filtersList());
//    TODO: merge existing filters and new filters

    Map<Long, FiltersList> shortMap = new HashMap<>(new HashMap<>());
    shortMap.put(addSubscriber.subscriberId(), filtersList);
    // TODO: notify subscriber logic below; pending tests to reside in @SubscriberFilterTriggerEventNotifierTest
    kindEventMap.forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            publisher.publishEvent(new SubscriberNotifierEvent<T>(shortMap, new AddNostrEvent<>(eventId, event, kind)))));

    subscribersFiltersMap.putIfAbsent(addSubscriber.subscriberId(), filtersList);
  }

  @EventListener
  public void removeSubscriberFilterHandler(RemoveSubscriberFilterEvent removeSubscriber) {
    subscribersFiltersMap.remove(removeSubscriber.subscriberId());
  }
}
