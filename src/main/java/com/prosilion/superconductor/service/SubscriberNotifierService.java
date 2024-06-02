package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import com.prosilion.superconductor.service.request.SubscriberService;
import com.prosilion.superconductor.util.FilterMatcher;
import lombok.SneakyThrows;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {
  private final SubscriberService subscriberService;
  private final FilterMatcher<T> filterMatcher;

  public SubscriberNotifierService(SubscriberService subscriberService, FilterMatcher<T> filterMatcher) {
    this.subscriberService = subscriberService;
    this.filterMatcher = filterMatcher;
  }

  public void subscriptionEventHandler(Long subscriberId, AddNostrEvent<T> addNostrEvent) {
    broadcastMatch(addNostrEvent, subscriberId, subscriberService.getFiltersList(subscriberId));
  }

  public void nostrEventHandler(AddNostrEvent<T> addNostrEvent) {
    Map<Long, List<Filters>> allFiltersOfAllSubscribers = subscriberService.getAllFiltersOfAllSubscribers();
    allFiltersOfAllSubscribers.forEach((subscriberId, filtersList) ->
        subscriptionEventHandler(subscriberId, addNostrEvent));
  }

  private void broadcastMatch(AddNostrEvent<T> addNostrEvent, Long subscriberId, List<Filters> filtersList) {
    filtersList.forEach(filters ->
        filterMatcher.intersectFilterMatches(filters, addNostrEvent).forEach(event ->
            broadcastToClients(new FireNostrEvent<>(subscriberId, event.event()))));
  }

  @SneakyThrows
  private void broadcastToClients(FireNostrEvent<T> fireNostrEvent) {
    subscriberService.broadcastToClients(fireNostrEvent);
  }
}