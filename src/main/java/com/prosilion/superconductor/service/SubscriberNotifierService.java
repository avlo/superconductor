package com.prosilion.superconductor.service;

import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import com.prosilion.superconductor.pubsub.SubscriberNotifierEvent;
import com.prosilion.superconductor.service.request.SubscriberService;
import com.prosilion.superconductor.util.FilterMatcher;
import nostr.api.NIP01;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {
  private final SubscriberService subscriberService;
  private final ApplicationEventPublisher publisher;
  private final FilterMatcher<T> filterMatcher;

  public SubscriberNotifierService(SubscriberService subscriberService, FilterMatcher<T> filterMatcher, ApplicationEventPublisher publisher) {
    this.subscriberService = subscriberService;
    this.filterMatcher = filterMatcher;
    this.publisher = publisher;
  }

  public void newEventHandler(SubscriberNotifierEvent<T> subscriberNotifierEvent) {
    Map<Long, AddNostrEvent<T>> eventsToSend = new HashMap<>();

    subscriberNotifierEvent.subscribersFiltersMap().forEach((subscriberId, subscriberIdFiltersList) ->
        subscriberIdFiltersList.getList().forEach(subscriberFilters ->
            filterMatcher.addEventMatch(subscriberFilters, subscriberNotifierEvent.addNostrEvent()).forEach(event ->
                eventsToSend.putIfAbsent(subscriberId, event))));

    eventsToSend.forEach((subscriberId, event) ->
        broadcastToClients(new FireNostrEvent<>(subscriberId, event.event())));
  }

  private void broadcastToClients(FireNostrEvent<T> fireNostrEvent) {
    EventMessage message = NIP01.createEventMessage(fireNostrEvent.event(), String.valueOf(fireNostrEvent.subscriberId()));
    Subscriber subscriber = subscriberService.get(fireNostrEvent.subscriberId());
    BroadcastMessageEvent<EventMessage> event = new BroadcastMessageEvent<>(subscriber.getSessionId(), message);
    publisher.publishEvent(event);
  }
}