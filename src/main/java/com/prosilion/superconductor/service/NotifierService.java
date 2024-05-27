package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.AddSubscriberFiltersEvent;
import lombok.Getter;
import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Getter
@Service
public class NotifierService<T extends GenericEvent> {
  private final EventNotifierService<T> eventNotifierService;
  private final SubscriberNotifierService<T> subscriberNotifierService;

  @Autowired
  public NotifierService(SubscriberNotifierService<T> subscriberNotifierService, EventNotifierService<T> eventNotifierService) {
    this.subscriberNotifierService = subscriberNotifierService;
    this.eventNotifierService = eventNotifierService;
  }

  public void nostrEventHandler(AddNostrEvent<T> addNostrEvent) {
    eventNotifierService.updateEventMap(addNostrEvent);
    subscriberNotifierService.newEventHandler(addNostrEvent);
  }

  @EventListener
  public void subscriptionEventHandler(AddSubscriberFiltersEvent addSubscriber) {
    Map<Long, FiltersList> subscriberFilterListMap = new HashMap<>(new HashMap<>());
    subscriberFilterListMap.put(addSubscriber.subscriberId(), addSubscriber.filtersList());
    eventNotifierService.getKindEventMap().forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            subscriberNotifierService.newEventHandler(new AddNostrEvent<>(kind, eventId, event))));
  }
}
