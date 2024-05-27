package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.AddSubscriberFiltersEvent;
import com.prosilion.superconductor.pubsub.SubscriberNotifierEvent;
import com.prosilion.superconductor.service.request.SubscriberFiltersService;
import lombok.Getter;
import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Service
public class NotifierService<T extends GenericEvent> {
  private final SubscriberNotifierService<T> subscriberNotifierService;
  private final EventNotifierService<T> eventNotifierService;
  private final SubscriberFiltersService subscriberFiltersService;

  @Autowired
  public NotifierService(SubscriberNotifierService<T> subscriberNotifierService, EventNotifierService<T> eventNotifierService, SubscriberFiltersService subscriberFiltersService) {
    this.subscriberNotifierService = subscriberNotifierService;
    this.eventNotifierService = eventNotifierService;
    this.subscriberFiltersService = subscriberFiltersService;
  }

  public void nostrEventHandler(AddNostrEvent<T> addNostrEvent) {
    eventNotifierService.updateEventMap(addNostrEvent);
    subscriberNotifierService.newEventHandler(new SubscriberNotifierEvent<>(subscriberFiltersService.getSubscribersFiltersMap(), addNostrEvent));
  }

  @EventListener
  public void addSubscriberFiltersHandler(AddSubscriberFiltersEvent addSubscriber) {
    FiltersList filtersList = Optional.ofNullable(subscriberFiltersService.getSubscriberFiltersList(addSubscriber.subscriberId())).orElse(addSubscriber.filtersList());

    Map<Long, FiltersList> subscriberFilterListMap = new HashMap<>(new HashMap<>());
    subscriberFilterListMap.put(addSubscriber.subscriberId(), filtersList);
    eventNotifierService.getKindEventMap().forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            subscriberNotifierService.newEventHandler(new SubscriberNotifierEvent<>(subscriberFilterListMap, new AddNostrEvent<>(kind, eventId, event)))));
  }
}
