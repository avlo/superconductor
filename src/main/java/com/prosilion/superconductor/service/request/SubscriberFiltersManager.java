package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilter;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SubscriberFiltersManager {
  private final FilterEntitiesService filterEntitiesService;
  private final SubscriberFilterRepository subscriberFilterRepository;

  @Autowired
  public SubscriberFiltersManager(FilterEntitiesService filterEntitiesService, SubscriberFilterRepository subscriberFilterRepository) {
    this.filterEntitiesService = filterEntitiesService;
    this.subscriberFilterRepository = subscriberFilterRepository;
  }

  public List<SubscriberFilter> getEntireSubscribersFilters() {
    return subscriberFilterRepository.findAll();
  }

  public List<Filters> getSubscriberFilters(@NonNull Long subscriberId) {
    List<Filters> filtersList = new ArrayList<>();
    getFilterIds(subscriberId).forEach(subscriberFilterId ->
        filtersList.add(
            filterEntitiesService.getFilters(subscriberFilterId)));
    return filtersList;
  }

  private List<Long> getFilterIds(Long subscriberId) {
    return subscriberFilterRepository.findAllBySubscriberId(subscriberId).stream().map(SubscriberFilter::getId).distinct().toList();
  }

  public void saveFilters(@NonNull Long subscriberId, @NonNull List<Filters> filtersList) {
    filtersList.forEach(filters ->
        filterEntitiesService.saveFilters(
            saveSubscriberFilter(subscriberId, filters),
            filters));
  }

  private Long saveSubscriberFilter(Long subscriberId, Filters filters) {
    return subscriberFilterRepository.save(
        new SubscriberFilter(subscriberId, filters.getSince(), filters.getUntil(), filters.getLimit())).getId();
  }

  public void removeAllFilters(Long subscriberId) {
    getFilterIds(subscriberId).forEach(
        filterEntitiesService::removeFilters);
    removeFilters(List.of(subscriberId));
  }

  private void removeFilters(List<Long> subscriberIds) {
    subscriberFilterRepository.deleteAllBySubscriberIdIn(subscriberIds);
  }
}
