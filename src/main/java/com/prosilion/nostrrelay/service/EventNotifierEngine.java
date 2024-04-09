package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.entity.EventEntity;
import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.AddSubscriberFiltersEvent;
import com.prosilion.nostrrelay.pubsub.RemoveSubscriberEvent;
import nostr.event.Kind;
import nostr.event.list.FiltersList;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class EventNotifierEngine {
  private final ApplicationEventPublisher publisher;
  private final HashMap<Long, FiltersList> subscriberIdFiltersMap;
  private final HashMap<Kind, EventEntity> eventMap;

  public EventNotifierEngine(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
    this.subscriberIdFiltersMap = new HashMap<>(); // use fast-hash map as/if necessary in the future
    this.eventMap = new HashMap<>();
  }

  @EventListener
  public void event(AddNostrEvent addNostrEvent) {
    eventMap.put(addNostrEvent.kind(), addNostrEvent.eventEntity());
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
