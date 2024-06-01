package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilter;
import jakarta.persistence.NoResultException;
import nostr.event.list.FiltersList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubscriberFiltersService {
  private final SubscriberFiltersManager subscriberFiltersManager;

  @Autowired
  public SubscriberFiltersService(SubscriberFiltersManager subscriberFiltersManager) {
    this.subscriberFiltersManager = subscriberFiltersManager;
  }

  public FiltersList getFiltersList(Long subscriberId) throws NoResultException {
    return getSubscriberFilters(subscriberId);
  }

  public Map<Long, FiltersList> getAllFiltersOfAllSubscribers() {
    List<SubscriberFilter> entireSubscribersFilters = subscriberFiltersManager.getEntireSubscribersFilters();
    return entireSubscribersFilters.stream().collect(
        Collectors.toMap(SubscriberFilter::getSubscriberId, subscriberFilter ->
            getSubscriberFilters(subscriberFilter.getSubscriberId())));
  }

  private FiltersList getSubscriberFilters(Long subscriberFilter) {
    FiltersList subscriberFilters = subscriberFiltersManager.getSubscriberFilters(subscriberFilter);
    return subscriberFilters;
  }

  @Async
  public void save(Long subscriberId, FiltersList filtersList) {
    subscriberFiltersManager.saveFilters(subscriberId, filtersList);
  }

  @Async
  public void deleteBySubscriberId(Long subscriberId) {
    subscriberFiltersManager.removeAllFilters(subscriberId);
  }
}
