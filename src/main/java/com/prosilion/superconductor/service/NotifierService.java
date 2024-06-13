package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.event.RedisEventEntityService;
import lombok.NonNull;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifierService<T extends GenericEvent> {
  private final SubscriberNotifierService<T> subscriberNotifierService;
  private final RedisEventEntityService<T> redisEventEntityService;

  @Autowired
  public NotifierService(SubscriberNotifierService<T> subscriberNotifierService, RedisEventEntityService<T> redisEventEntityService) {
    this.subscriberNotifierService = subscriberNotifierService;
    this.redisEventEntityService = redisEventEntityService;
  }

  public void nostrEventHandler(@NonNull AddNostrEvent<T> addNostrEvent) {
    subscriberNotifierService.nostrEventHandler(addNostrEvent);
  }

  public void subscriptionEventHandler(@NonNull Long subscriberId) {
    redisEventEntityService.getAll().forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            subscriberNotifierService.subscriptionEventHandler(subscriberId, new AddNostrEvent<>(event))));
  }
}
