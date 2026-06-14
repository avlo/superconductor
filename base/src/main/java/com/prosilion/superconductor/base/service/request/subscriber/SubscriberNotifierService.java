package com.prosilion.superconductor.base.service.request.subscriber;

import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.base.service.request.pubsub.EoseNotice;
import com.prosilion.superconductor.base.service.request.pubsub.FireNostrEvent;
import com.prosilion.superconductor.base.util.FilterMatcher;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriberNotifierService {
  private final AbstractSubscriberService abstractSubscriberService;
  private final FilterMatcher filterMatcher;

  @Autowired
  public SubscriberNotifierService(
     @NonNull AbstractSubscriberService abstractSubscriberService,
     @NonNull FilterMatcher filterMatcher) {
    this.abstractSubscriberService = abstractSubscriberService;
    this.filterMatcher = filterMatcher;
  }

  protected void nostrEventHandler(@NonNull AddNostrEvent addNostrEvent) {
    abstractSubscriberService.getAllFiltersOfAllSubscribers().forEach((subscriberSessionHash, filtersList) ->
       broadcastMatch(subscriberSessionHash, addNostrEvent));
  }

  protected void newSubscriptionHandler(@NonNull Long subscriberSessionHash, @NonNull AddNostrEvent addNostrEvent) {
    broadcastMatch(subscriberSessionHash, addNostrEvent);
  }

  private void broadcastMatch(Long subscriberSessionHash, AddNostrEvent addNostrEvent) {
    abstractSubscriberService.getFiltersList(subscriberSessionHash).forEach(userFilters ->
       filterMatcher.intersectFilterMatches(userFilters, addNostrEvent).ifPresent(event ->
          broadcastToClients(new FireNostrEvent(
             subscriberSessionHash,
             abstractSubscriberService.get(subscriberSessionHash).getSubscriberId(),
             event.event()))));
  }

  private void broadcastToClients(@NonNull FireNostrEvent fireNostrEvent) {
    abstractSubscriberService.broadcastToClients(fireNostrEvent);
  }

  protected void broadcastEose(@NonNull Long subscriberSessionHash) {
    abstractSubscriberService.broadcastToClients(new EoseNotice(
       subscriberSessionHash,
       abstractSubscriberService.get(subscriberSessionHash).getSubscriberId()));
  }
}
