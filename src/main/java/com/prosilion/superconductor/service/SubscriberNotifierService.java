package com.prosilion.superconductor.service;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import com.prosilion.superconductor.service.request.SubscriberService;
import com.prosilion.superconductor.util.FilterMatcher;
import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;
import org.springframework.stereotype.Service;

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
    subscriberService.getCrazinessAllSubscribersFilterMap().forEach((subscriberId, filtersList) ->
        broadcastMatch(addNostrEvent, subscriberId, filtersList));
  }

  private void broadcastMatch(AddNostrEvent<T> addNostrEvent, Long subscriberId, FiltersList filtersList) {
    filtersList.getList().forEach(filters ->
        filterMatcher.intersectFilterMatches(filters, addNostrEvent).forEach(event ->
            subscriberService.broadcastToClients(new FireNostrEvent<>(subscriberId, event.event()))));
  }
}