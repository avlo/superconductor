package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import lombok.Getter;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    subscriberNotifierService.nostrEventHandler(addNostrEvent);
  }

  public void subscriptionEventHandler(Long subscriberId) {
    eventNotifierService.getKindEventMap().forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            subscriberNotifierService.subscriptionEventHandler(subscriberId, new AddNostrEvent<>(kind, eventId, event))));
  }
}
