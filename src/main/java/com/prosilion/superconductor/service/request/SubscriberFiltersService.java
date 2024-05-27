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

  private final Map<Long, FiltersList> subscribersFiltersMap = new HashMap<>();

  @Autowired
  public SubscriberFiltersService(SubscriberFiltersManager subscriberFiltersManager, ApplicationEventPublisher publisher) {
    this.subscriberFiltersManager = subscriberFiltersManager;
    this.publisher = publisher;
  }

  public FiltersList getSubscriberFilters(Long subscriberId) {
    return subscriberFiltersManager.getSubscriberFilters(subscriberId);
  }

  public Map<Long, FiltersList> getYouBetterRefactorEntireSubscriberFiltersMap() {
    return subscribersFiltersMap;
  }

  @Async
  public void save(Long subscriberId, FiltersList filtersList) {
    subscriberFiltersManager.saveFilters(subscriberId, filtersList);
    subscribersFiltersMap.put(subscriberId, filtersList);
    publisher.publishEvent(new AddSubscriberFiltersEvent(subscriberId, filtersList));
  }

  @Async
  public void deleteBySubscriberId(Long subscriberId) {
    subscribersFiltersMap.remove(subscriberId);
    subscriberFiltersManager.removeAllFilters(subscriberId);
  }
}
