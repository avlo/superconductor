package com.prosilion.superconductor.service.request;

import nostr.event.list.FiltersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriberFiltersService {
  private final SubscriberFiltersManager subscriberFiltersManager;

  private final Map<Long, FiltersList> subscribersFiltersMap = new HashMap<>();

  @Autowired
  public SubscriberFiltersService(SubscriberFiltersManager subscriberFiltersManager) {
    this.subscriberFiltersManager = subscriberFiltersManager;
  }

  public FiltersList getFiltersList(Long subscriberId) {
    return subscriberFiltersManager.getSubscriberFilters(subscriberId);
  }

  public Map<Long, FiltersList> getYouBetterRefactorEntireSubscriberFiltersMap() {
    return subscribersFiltersMap;
  }

  @Async
  public void save(Long subscriberId, FiltersList filtersList) {
    subscriberFiltersManager.saveFilters(subscriberId, filtersList);
    subscribersFiltersMap.put(subscriberId, filtersList);
  }

  @Async
  public void deleteBySubscriberId(Long subscriberId) {
    subscribersFiltersMap.remove(subscriberId);
    subscriberFiltersManager.removeAllFilters(subscriberId);
  }
}
