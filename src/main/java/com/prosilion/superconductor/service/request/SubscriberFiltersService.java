package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.pubsub.AddSubscriberFiltersEvent;
import nostr.event.list.FiltersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriberFiltersService {
  private final SubscriberFiltersManager subscriberFiltersManager;
  private final ApplicationEventPublisher publisher;

  // TODO: below currently stores:
//
//      Map<SubscriberId, FiltersList Object>
//
//  should instead store:
//      Map<SubscriberId, FiltersList Id>
//
//  upon which a service lookup and filters list population occurs
//
  private final Map<Long, FiltersList> subscribersFiltersMap;

  @Autowired
  public SubscriberFiltersService(SubscriberFiltersManager subscriberFiltersManager, ApplicationEventPublisher publisher) {
    this.subscribersFiltersMap = new HashMap<>(new HashMap<>()); // use fast-hash map as/if necessary in the future
    this.subscriberFiltersManager = subscriberFiltersManager;
    this.publisher = publisher;
  }

  @Async
  public void save(Long subscriberId, FiltersList filtersList) {
    subscriberFiltersManager.saveFilters(subscriberId, filtersList);
    subscribersFiltersMap.put(subscriberId, filtersList);
    publisher.publishEvent(new AddSubscriberFiltersEvent(subscriberId, filtersList));
  }

  @Async
  public void deleteBySubscriberId(Long subscriberId) {
    subscriberFiltersManager.removeAllFilters(subscriberId);
    subscribersFiltersMap.remove(subscriberId);
  }
}
