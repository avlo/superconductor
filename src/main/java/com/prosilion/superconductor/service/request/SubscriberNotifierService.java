package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.request.pubsub.FireNostrEvent;
import com.prosilion.superconductor.util.FilterMatcher;
import lombok.NonNull;
import lombok.SneakyThrows;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {
  private final AbstractSubscriberService abstractSubscriberService;
  private final FilterMatcher filterMatcher;

  @Autowired
  public SubscriberNotifierService(AbstractSubscriberService abstractSubscriberService, FilterMatcher filterMatcher) {
    this.abstractSubscriberService = abstractSubscriberService;
    this.filterMatcher = filterMatcher;
  }

  public void nostrEventHandler(@NonNull AddNostrEvent<T> addNostrEvent) {
    abstractSubscriberService.getAllFiltersOfAllSubscribers().forEach((subscriberSessionHash, filtersList) ->
        subscriptionEventHandler(subscriberSessionHash, addNostrEvent));
  }

  public void subscriptionEventHandler(@NonNull Long subscriberSessionHash, @NonNull AddNostrEvent<T> addNostrEvent) {
    broadcastMatch(addNostrEvent, subscriberSessionHash);
  }

  private void broadcastMatch(AddNostrEvent<T> addNostrEvent, Long subscriberSessionHash) {
    abstractSubscriberService.getFiltersList(subscriberSessionHash).forEach(filters ->
        filterMatcher.intersectFilterMatches(filters, (AddNostrEvent<GenericEvent>) addNostrEvent).forEach(event ->
            broadcastToClients((FireNostrEvent<T>) new FireNostrEvent<>(
                subscriberSessionHash,
                abstractSubscriberService.get(subscriberSessionHash).getSubscriberId(),
                event.event()))));
  }

  @SneakyThrows
  private void broadcastToClients(@NonNull FireNostrEvent<T> fireNostrEvent) {
    abstractSubscriberService.broadcastToClients(fireNostrEvent);
  }
}