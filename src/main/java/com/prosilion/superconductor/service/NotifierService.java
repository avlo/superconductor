package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import lombok.Getter;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Service
public class NotifierService<T extends GenericEvent> {
  private final SubscriberNotifierService<T> subscriberNotifierService;

  @Autowired
  public NotifierService(SubscriberNotifierService<T> subscriberNotifierService) {
    this.subscriberNotifierService = subscriberNotifierService;
  }

  public void nostrEventHandler(AddNostrEvent<T> addNostrEvent) {
    subscriberNotifierService.nostrEventHandler(addNostrEvent);
  }

  public void subscriptionEventHandler(Long subscriberId, AddNostrEvent<T> addNostrEvent) {
    subscriberNotifierService.subscriptionEventHandler(subscriberId, addNostrEvent);
  }
}
