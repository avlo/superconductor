package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import com.prosilion.superconductor.service.request.SubscriberService;
import com.prosilion.superconductor.util.FilterMatcher;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {
  private final SubscriberService subscriberService;
  private final FilterMatcher<T> filterMatcher;

  public SubscriberNotifierService(SubscriberService subscriberService, FilterMatcher<T> filterMatcher) {
    this.subscriberService = subscriberService;
    this.filterMatcher = filterMatcher;
  }

  public void newEventHandler(AddNostrEvent<T> addNostrEvent) {
    Map<Long, AddNostrEvent<T>> eventsToSend = new HashMap<>();

    subscriberService.getCrazinessAllSubscribersFilterMap().forEach((subscriberId, subscriberIdFiltersList) ->
        subscriberIdFiltersList.getList().forEach(subscriberFilters ->
            filterMatcher.addEventMatch(subscriberFilters, addNostrEvent).forEach(event ->
                eventsToSend.putIfAbsent(subscriberId, event))));

    eventsToSend.forEach((subscriberId, event) ->
        subscriberService.broadcastToClients(new FireNostrEvent<>(subscriberId, event.event())));
  }
}