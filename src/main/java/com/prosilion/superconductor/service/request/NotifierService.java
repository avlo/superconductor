package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.service.event.RedisCache;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.NonNull;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifierService<T extends GenericEvent> {
  private final SubscriberNotifierService<T> subscriberNotifierService;
  private final RedisCache<T> redisCache;

  @Autowired
  public NotifierService(SubscriberNotifierService<T> subscriberNotifierService, RedisCache<T> redisCache) {
    this.subscriberNotifierService = subscriberNotifierService;
    this.redisCache = redisCache;
  }

  public void nostrEventHandler(@NonNull AddNostrEvent<T> addNostrEvent) {
    subscriberNotifierService.nostrEventHandler(addNostrEvent);
  }

  public void subscriptionEventHandler(@NonNull Long subscriberSessionHash) {
    redisCache.getAll().forEach((kind, eventMap) ->
        eventMap.forEach((eventId, event) ->
            subscriberNotifierService.newSubscriptionHandler(subscriberSessionHash, new AddNostrEvent<>(event))));
    subscriberNotifierService.broadcastEose(subscriberSessionHash);
  }
}
