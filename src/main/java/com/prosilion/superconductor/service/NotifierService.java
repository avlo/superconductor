package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.event.KindEventMapService;
import lombok.Getter;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Service
public class NotifierService<T extends GenericEvent> {
  private final SubscriberNotifierService<T> subscriberNotifierService;
  private final KindEventMapService<T> kindEventMapService;

  @Autowired
  public NotifierService(SubscriberNotifierService<T> subscriberNotifierService, KindEventMapService<T> kindEventMapService) {
    this.subscriberNotifierService = subscriberNotifierService;
    this.kindEventMapService = kindEventMapService;
  }

  public void nostrEventHandler(AddNostrEvent<T> addNostrEvent) {
    kindEventMapService.updateEventMap(addNostrEvent);
    subscriberNotifierService.nostrEventHandler(addNostrEvent);
  }

  public void subscriptionEventHandler(Long subscriberId) {
    kindEventMapService.getGottaProperlyDAOImplThisKindEventMap().forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            subscriberNotifierService.subscriptionEventHandler(subscriberId, new AddNostrEvent<>(kind, eventId, event))));
  }
}
