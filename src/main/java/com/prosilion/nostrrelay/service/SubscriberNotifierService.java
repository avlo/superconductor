package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.FireNostrEvent;
import com.prosilion.nostrrelay.pubsub.SubscriberNotifierEvent;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    Map<Long, FiltersList> subscribersFiltersMap = subscriberNotifierEvent.subscribersFiltersMap();
    AddNostrEvent<T> addNostrEvent = subscriberNotifierEvent.addNostrEvent();

    // TODO: prudent replace all below parallelizable
    Map<Long, AddNostrEvent<T>> eventsToSend = new HashMap<>();
    subscribersFiltersMap.forEach((subscriberId, subscriberIdFiltersList) -> {
      subscriberIdFiltersList.getList().forEach(subscriberFilters ->
          addMatch(subscriberFilters, addNostrEvent).forEach(event ->
              eventsToSend.putIfAbsent(subscriberId, event)));
    });
    eventsToSend.forEach((subscriberId, event) ->
        publisher.publishEvent(new FireNostrEvent<T>(subscriberId, event.event())));
  }

  private List<AddNostrEvent<T>> addMatch(Filters subscriberFilters, AddNostrEvent<T> eventToCheck) {

    List<AddNostrEvent<T>> matches = new ArrayList<>();

    // TODO: convert to stream
    for (GenericEvent subscriberEvent : subscriberFilters.getEvents().getList()) {
      if (subscriberEvent.getId().equals(eventToCheck.event().getContent())) {
        matches.add(eventToCheck);
      }
    }

    List<AddNostrEvent<T>> matches2 = new ArrayList<>();
    List<GenericEvent> genericEventList = subscriberFilters.getEvents().getList();
    genericEventList.stream().filter(event -> event.getId().equals(eventToCheck.event().getContent())).anyMatch(event -> matches2.add(eventToCheck));

    System.out.println("111111111111111111");
    System.out.println("111111111111111111");
    System.out.println(matches);
    System.out.println("------------------");
    System.out.println(matches2);
    System.out.println("111111111111111111");
    System.out.println("111111111111111111");

    return matches;
  }
}
