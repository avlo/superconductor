package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.join.subscriber.AbstractFilterType;
import com.prosilion.superconductor.entity.join.subscriber.FilterPlugin;
import com.prosilion.superconductor.repository.join.subscriber.AbstractSubscriberFilterTypeJoinRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FilterEntitiesService<
    T extends AbstractSubscriberFilterTypeJoinRepository<U>,
    U extends AbstractFilterType> {
  private final List<FilterPlugin<T, U>> filterPlugins;

  @Autowired
  public FilterEntitiesService(List<FilterPlugin<T, U>> filterPlugins) {
    this.filterPlugins = filterPlugins;
  }

  public Filters getFilters(@NonNull Long filterId) {
    Filters filters = new Filters();
    filterPlugins.forEach(plugin ->
        plugin.appendFilters(filterId, filters));
    return filters;
  }

  public void saveFilters(@NonNull Long filterId, @NonNull Filters filters) {
    filterPlugins.forEach(plugin ->
        plugin.saveFilters(filterId, filters));
  }

  public void removeFilters(@NonNull Long filterId) {
    filterPlugins.forEach(plugin ->
        plugin.removeFilters(filterId));
  }
}