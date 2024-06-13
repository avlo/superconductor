package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import com.prosilion.superconductor.service.request.SubscriberService;
import com.prosilion.superconductor.util.FilterMatcher;
import lombok.NonNull;
import lombok.SneakyThrows;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {
  private final SubscriberService subscriberService;
  private final FilterMatcher<T> filterMatcher;

  @Autowired
  public SubscriberNotifierService(SubscriberService subscriberService, FilterMatcher<T> filterMatcher) {
    this.subscriberService = subscriberService;
    this.filterMatcher = filterMatcher;
  }

  public void nostrEventHandler(@NonNull AddNostrEvent<T> addNostrEvent) {
    subscriberService.getAllFiltersOfAllSubscribers().forEach((subscriberId, filtersList) ->
        subscriptionEventHandler(subscriberId, addNostrEvent));
  }

  public void subscriptionEventHandler(@NonNull Long subscriberId, @NonNull AddNostrEvent<T> addNostrEvent) {
    broadcastMatch(addNostrEvent, subscriberId, subscriberService.getFiltersList(subscriberId));
  }

  private void broadcastMatch(AddNostrEvent<T> addNostrEvent, Long subscriberId, List<Filters> filtersList) {
    filtersList.forEach(filters ->
        filterMatcher.intersectFilterMatches(filters, addNostrEvent).forEach(event ->
            broadcastToClients(new FireNostrEvent<>(subscriberId, event.event()))));
  }

  @SneakyThrows
  private void broadcastToClients(@NonNull FireNostrEvent<T> fireNostrEvent) {
    subscriberService.broadcastToClients(fireNostrEvent);
  }
}