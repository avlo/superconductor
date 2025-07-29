package com.prosilion.superconductor.base.service.request;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import java.util.Map;
import org.springframework.lang.NonNull;

public class NotifierService {
  private final SubscriberNotifierService subscriberNotifierService;
  private final CacheIF cacheIF;

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
    Map<Kind, Map<?, ? extends EventIF>> allMappedByKind = cacheIF.getAllMappedByKind();
    allMappedByKind
        .forEach((kind, eventMap) ->
            eventMap.forEach((eventId, event) ->
                subscriberNotifierService.newSubscriptionHandler(subscriberSessionHash, new AddNostrEvent(event))));
    subscriberNotifierService.broadcastEose(subscriberSessionHash);
  }
}
