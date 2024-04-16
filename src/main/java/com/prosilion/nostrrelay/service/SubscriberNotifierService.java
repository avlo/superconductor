package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.SubscriberNotifier;
import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {

  @EventListener
  public void newEventHandler(SubscriberNotifier<T> subscriberNotifier) {
    Map<Long, FiltersList> subscribersFiltersMap = subscriberNotifier.getSubscribersFiltersMap();
    AddNostrEvent<T> addNostrEvent = subscriberNotifier.getAddNostrEvent();

    // TODO: all below parallelizable
    // iterate subscribers map
    // get the subscribers filters
    // for each subscribers filters, iterate over each filter type
    // for each filter type, see if it matches the relevant event attribute
    // if there's a match, send event to subscriber
  }
}
