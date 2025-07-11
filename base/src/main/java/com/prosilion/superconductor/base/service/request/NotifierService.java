package com.prosilion.superconductor.base.service.request;

import com.prosilion.superconductor.base.service.event.type.CacheIF;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class NotifierService {
  private final SubscriberNotifierService subscriberNotifierService;
  private final CacheIF cacheIF;

  @Autowired
  public NotifierService(
      SubscriberNotifierService subscriberNotifierService,
      CacheIF cacheIF) {
    this.subscriberNotifierService = subscriberNotifierService;
    this.cacheIF = cacheIF;
  }

  public void nostrEventHandler(@NonNull AddNostrEvent addNostrEvent) {
    subscriberNotifierService.nostrEventHandler(addNostrEvent);
  }

  public void subscriptionEventHandler(@NonNull Long subscriberSessionHash) {
//    TODO: below getAll should be cached/Redis
    cacheIF.getAll()
        .forEach((kind, eventMap) ->
            eventMap.forEach((eventId, event) ->
                subscriberNotifierService.newSubscriptionHandler(subscriberSessionHash, new AddNostrEvent(event))));
    subscriberNotifierService.broadcastEose(subscriberSessionHash);
  }
}
