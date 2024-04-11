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
import java.util.Optional;

@Service
public class EventNotifierEngine<T extends GenericEvent> {
  private final Map<Long, FiltersList> subscribersFiltersMap;
  private final Map<Kind, Map<Long, T>> kindEventMap;

  public EventNotifierEngine() {
    this.subscribersFiltersMap = new HashMap<>(new HashMap<>()); // use fast-hash map as/if necessary in the future
    this.kindEventMap = new HashMap<>();
  }

  @EventListener
  public void nostrEventHandler(AddNostrEvent<T> addNostrEvent) {
    Map<Long, T> map = Optional.ofNullable(kindEventMap.get(addNostrEvent.getKind())).orElse(new HashMap<>());
    map.putIfAbsent(addNostrEvent.getId(), addNostrEvent.getEventIdEventMap().get(addNostrEvent.getId()));
    kindEventMap.put(addNostrEvent.getKind(), map);
  }

  @EventListener
  public void addSubscriberFiltersHandler(AddSubscriberFiltersEvent addSubscriber) {
    subscribersFiltersMap.put(addSubscriber.subscriberId(), addSubscriber.filtersList());
  }

  @EventListener
  public void removeSubscriberHandler(RemoveSubscriberEvent removeSubscriber) {
    subscribersFiltersMap.remove(removeSubscriber.subscriberId());
  }
}
