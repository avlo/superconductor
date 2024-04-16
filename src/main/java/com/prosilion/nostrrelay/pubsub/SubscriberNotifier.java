package com.prosilion.nostrrelay.pubsub;

import lombok.Getter;
import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;

import java.util.Map;

@Getter
public class SubscriberNotifier<T extends GenericEvent> {
  private final Map<Long, FiltersList> subscribersFiltersMap;
  private final AddNostrEvent<T> addNostrEvent;

  public SubscriberNotifier(Map<Long, FiltersList> subscribersFiltersMap, AddNostrEvent<T> addNostrEvent) {
    this.subscribersFiltersMap = subscribersFiltersMap;
    this.addNostrEvent = addNostrEvent;
  }
}
