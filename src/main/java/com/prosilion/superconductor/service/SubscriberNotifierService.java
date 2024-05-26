package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import com.prosilion.superconductor.pubsub.SubscriberNotifierEvent;
import com.prosilion.superconductor.util.FilterMatcher;
import nostr.event.impl.GenericEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {
  private final ApplicationEventPublisher publisher;
  private final FilterMatcher<T> filterMatcher;

  public SubscriberNotifierService(ApplicationEventPublisher publisher, FilterMatcher<T> filterMatcher) {
    this.publisher = publisher;
    this.filterMatcher = filterMatcher;
  }

  public void notifySubscriber(AddNostrEvent<T> subscriberNotifierEvent) {
  }

  public void notifySubscriber(SubscriberNotifierEvent<T> subscriberNotifierEvent) {
    Map<Long, AddNostrEvent<T>> eventsToSend = new HashMap<>();

    subscriberNotifierEvent.subscribersFiltersMap().forEach((subscriberId, subscriberIdFiltersList) ->
        subscriberIdFiltersList.getList().forEach(subscriberFilters ->
            filterMatcher.addEventMatch(subscriberFilters, subscriberNotifierEvent.addNostrEvent()).forEach(event ->
                eventsToSend.putIfAbsent(subscriberId, event))));

    eventsToSend.forEach((subscriberId, event) ->
        publisher.publishEvent(new FireNostrEvent<T>(subscriberId, event.event())));
  }
}