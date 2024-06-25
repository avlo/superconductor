package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.join.subscriber.AbstractSubscriberFilterType;
import com.prosilion.superconductor.entity.join.subscriber.FilterPlugin;
import com.prosilion.superconductor.repository.join.subscriber.AbstractSubscriberFilterTypeJoinRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilterEntitiesService<
    S extends AbstractSubscriberFilterType,
    T extends AbstractSubscriberFilterTypeJoinRepository<S>> {
  private final List<FilterPlugin<S, T>> filterPlugins;

  @Autowired
  public FilterEntitiesService(List<FilterPlugin<S, T>> filterPlugins) {
    this.filterPlugins = filterPlugins;
  }

  public List<AbstractSubscriberFilterType> getFilters(@NonNull Long filterId) {
    return filterPlugins.stream().map(plugin ->
            plugin.getFilters(filterId))
        .flatMap(List::stream).distinct().collect(Collectors.toList());
  }

  public void saveFilters(@NonNull Long filterId, @NonNull List<S> filters) {
    filterPlugins.forEach(plugin ->
        filters.stream().filter(filter ->
                filter.getCode().equalsIgnoreCase(plugin.getCode()))
            .forEach(filter -> {
              filter.setFilterId(filterId);
              plugin.saveFilter(filter);
            }));
  }
}