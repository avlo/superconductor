package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilter;
import jakarta.persistence.NoResultException;
import lombok.NonNull;
import nostr.event.impl.Filters;
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

  public List<Filters> getFiltersList(@NonNull Long subscriberId) throws NoResultException {
    return getSubscriberFilters(subscriberId);
  }

  public Map<Long, List<Filters>> getAllFiltersOfAllSubscribers() {
    return subscriberFiltersManager.getEntireSubscribersFilters().stream().collect(
        Collectors.toMap(SubscriberFilter::getSubscriberId, subscriberFilter ->
            getSubscriberFilters(subscriberFilter.getSubscriberId())));
  }

  private List<Filters> getSubscriberFilters(@NonNull Long subscriberFilter) {
    return subscriberFiltersManager.getSubscriberFilters(subscriberFilter);
  }

  @Async
  public void save(@NonNull Long subscriberId, @NonNull List<Filters> filtersList) {
    subscriberFiltersManager.saveFilters(subscriberId, filtersList);
  }

  @Async
  public void deleteBySubscriberId(@NonNull Long subscriberId) {
    subscriberFiltersManager.removeAllFilters(subscriberId);
  }
}
