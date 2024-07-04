package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import com.prosilion.superconductor.service.request.CachedSubscriberService;
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
  public SubscriberNotifierService(CachedSubscriberService abstractSubscriberService, FilterMatcher filterMatcher) {
    this.abstractSubscriberService = abstractSubscriberService;
    this.filterMatcher = filterMatcher;
  }

  public void nostrEventHandler(@NonNull AddNostrEvent<T> addNostrEvent) {
    abstractSubscriberService.getAllFiltersOfAllSubscribers().forEach((subscriberId, filtersList) ->
        subscriptionEventHandler(subscriberId, addNostrEvent));
  }

  public void subscriptionEventHandler(@NonNull Long subscriberId, @NonNull AddNostrEvent<T> addNostrEvent) {
    broadcastMatch(addNostrEvent, subscriberId);
  }

  private void broadcastMatch(AddNostrEvent<T> addNostrEvent, Long subscriberId) {
    abstractSubscriberService.getFiltersList(subscriberId).forEach(filters ->
        filterMatcher.intersectFilterMatches(filters, (AddNostrEvent<GenericEvent>) addNostrEvent).forEach(event ->
            broadcastToClients((FireNostrEvent<T>) new FireNostrEvent<>(subscriberId, event.event()))));
  }

  @SneakyThrows
  private void broadcastToClients(@NonNull FireNostrEvent<T> fireNostrEvent) {
    abstractSubscriberService.broadcastToClients(fireNostrEvent);
  }
}