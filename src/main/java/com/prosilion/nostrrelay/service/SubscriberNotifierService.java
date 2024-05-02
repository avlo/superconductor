package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.FireNostrEvent;
import com.prosilion.nostrrelay.pubsub.SubscriberNotifierEvent;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {
  private final ApplicationEventPublisher publisher;

  public SubscriberNotifierService(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  @EventListener
  public void newEventHandler(SubscriberNotifierEvent<T> subscriberNotifierEvent) {
    Map<Long, AddNostrEvent<T>> eventsToSend = new HashMap<>();

    subscriberNotifierEvent.subscribersFiltersMap().forEach((subscriberId, subscriberIdFiltersList) ->
        subscriberIdFiltersList.getList().forEach(subscriberFilters ->
            addMatch(subscriberFilters, subscriberNotifierEvent.addNostrEvent()).forEach(event ->
                eventsToSend.putIfAbsent(subscriberId, event))));

    eventsToSend.forEach((subscriberId, event) ->
        publisher.publishEvent(new FireNostrEvent<T>(subscriberId, event.event())));
  }

  private List<AddNostrEvent<T>> addMatch(Filters subscriberFilters, AddNostrEvent<T> eventToCheck) {
    return subscriberFilters.getEvents().getList()
        .stream()
        .filter(event -> event.getId().equals(eventToCheck.event().getContent()))
        .map(event -> eventToCheck)
        .toList();
  }
}