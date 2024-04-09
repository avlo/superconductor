package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.AddSubscriberFiltersEvent;
import com.prosilion.nostrrelay.pubsub.RemoveSubscriberEvent;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EventNotifierEngine<T extends GenericEvent> {
  private final Map<Long, FiltersList> subscriberIdFiltersMap;
  private final Map<Kind, Map<Long, T>> kindEventMap;

  public EventNotifierEngine() {
    this.subscriberIdFiltersMap = new HashMap<>(); // use fast-hash map as/if necessary in the future
    this.kindEventMap = new HashMap<>();
  }

  @EventListener
  public void event(AddNostrEvent<T> addNostrEvent) {
    kindEventMap.put(addNostrEvent.getKind(), addNostrEvent.getEventIdEventMap());
    System.out.println("11111111111111111");
    System.out.println("11111111111111111");
  }

  @EventListener
  public void event(AddSubscriberFiltersEvent addSubscriber) {
    subscriberIdFiltersMap.put(addSubscriber.subscriberId(), addSubscriber.filtersList());
  }

  @EventListener
  public void event(RemoveSubscriberEvent removeSubscriber) {
    subscriberIdFiltersMap.remove(removeSubscriber.subscriberId());
  }
}
