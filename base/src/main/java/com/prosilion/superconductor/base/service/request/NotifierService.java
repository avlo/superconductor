package com.prosilion.superconductor.base.service.request;

import com.prosilion.nostr.crypto.NostrUtil;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.service.GenericEventKind;
import com.prosilion.superconductor.base.service.event.service.GenericEventKindIF;
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
        .map(this::convertEntityToGenericEventKindIF).toList();
    
    all.forEach(event ->
        subscriberNotifierService.newSubscriptionHandler(subscriberSessionHash, new AddNostrEvent(event)));
    
    subscriberNotifierService.broadcastEose(subscriberSessionHash);
  }

  private GenericEventKindIF convertEntityToGenericEventKindIF(EventIF eventIF) {
    final Signature signature = new Signature();
    signature.setRawData(NostrUtil.hex128ToBytes(eventIF.getSignature().toString()));
    return new GenericEventKind(
        eventIF.getId(),
        eventIF.getPublicKey(),
        eventIF.getCreatedAt(),
        eventIF.getKind(),
        eventIF.getTags(),
        eventIF.getContent(),
        signature);
  }
}
