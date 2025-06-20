package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.service.event.type.RedisCache;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventKindIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifierService<T extends GenericEventKindIF> {
  private final SubscriberNotifierService<T> subscriberNotifierService;
  private final RedisCache<T> redisCache;

  @Autowired
  public NotifierService(
      SubscriberNotifierService<T> subscriberNotifierService,
      RedisCache<T> redisCache) {
    this.subscriberNotifierService = subscriberNotifierService;
    this.redisCache = redisCache;
  }

  public void nostrEventHandler(@NonNull AddNostrEvent<T> addNostrEvent) {
    subscriberNotifierService.nostrEventHandler(addNostrEvent);
  }

  public void subscriptionEventHandler(@NonNull Long subscriberSessionHash) {
//    TODO: below getAll should be cached/Redis
    redisCache.getAll()
        .forEach((kind, eventMap) ->
            eventMap.forEach((eventId, event) ->
                subscriberNotifierService.newSubscriptionHandler(subscriberSessionHash, new AddNostrEvent<>(event))));
    subscriberNotifierService.broadcastEose(subscriberSessionHash);
  }
}
