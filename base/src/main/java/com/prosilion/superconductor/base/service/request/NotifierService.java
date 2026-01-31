package com.prosilion.superconductor.base.service.request;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class NotifierService {
  private final SubscriberNotifierService subscriberNotifierService;
  private final CacheServiceIF cacheServiceIF;

  @Autowired
  public NotifierService(
      @NonNull SubscriberNotifierService subscriberNotifierService,
      @NonNull CacheServiceIF cacheServiceIF) {
    this.subscriberNotifierService = subscriberNotifierService;
    this.cacheServiceIF = cacheServiceIF;
  }

  public void nostrEventHandler(@NonNull AddNostrEvent addNostrEvent) {
    subscriberNotifierService.nostrEventHandler(addNostrEvent);
  }

  public void subscriptionEventHandler(@NonNull Long subscriberSessionHash) {
    List<? extends EventIF> cacheServiceIFAll = cacheServiceIF.getAll();

    List<? extends EventIF> all = cacheServiceIFAll
        .stream()
        .map(EventIF::asGenericEventRecord).toList();

    all.forEach(event ->
        subscriberNotifierService.newSubscriptionHandler(subscriberSessionHash, new AddNostrEvent(event)));

    subscriberNotifierService.broadcastEose(subscriberSessionHash);
  }
}
