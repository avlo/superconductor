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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    Map<Long, AddNostrEvent<T>> eventsToSend = new HashMap<>();


    subscribersFiltersMap.forEach((subscriberId, subscriberIdFiltersList) -> {
      subscriberIdFiltersList.getList().forEach(subscriberFilters ->
          addMatch(subscriberFilters, addNostrEvent).forEach(event ->
              eventsToSend.putIfAbsent(subscriberId, event)));
    });


/**  https://howtodoinjava.com/java8/stream-map-example/
 *   https://howtodoinjava.com/java8/collect-stream-to-map/
 *   https://www.geeksforgeeks.org/stream-map-java-examples/
 *   https://www.techiedelight.com/convert-stream-to-map-java-8/
 *   https://stackoverflow.com/questions/33606014/collect-stream-into-a-hashmap-with-lambda-in-java-8
 *
*/
    Map<Long, AddNostrEvent<T>> eventsToSend2 = subscribersFiltersMap.entrySet()
        .stream()
        .filter(e -> e.getValue().getList()
            .stream()
            .map(subscriberFilters ->
                addMatch(
                    subscriberFilters,
                    addNostrEvent
                ))
            .map(addNostrEvents -> eventsToSend)
            .to
        );

    eventsToSend.forEach((subscriberId, event) ->
        publisher.publishEvent(new FireNostrEvent<T>(subscriberId, event.event())));
  }


  private List<AddNostrEvent<T>> addMatch(Filters subscriberFilters, AddNostrEvent<T> eventToCheck) {
    return subscriberFilters.getEvents().getList()
        .stream()
        .filter(event -> event.getId().equals(eventToCheck.event().getContent()))
        .map(event -> eventToCheck)
        .collect(Collectors.toList());
  }
}