package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.SubscriberNotifier;
import nostr.event.impl.GenericEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {

  @EventListener
  public void newEventHandler(SubscriberNotifier<T> subscriberNotifier) {
  }
}
