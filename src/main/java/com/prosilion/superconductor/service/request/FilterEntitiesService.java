package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.join.subscriber.FilterPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FilterEntitiesService {
  private final List<FilterPlugin> filterPlugins;

  @Autowired
  public FilterEntitiesService(List<FilterPlugin> filterPlugins) {
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
        plugin.saveFilter(filterId, filters));
  }

  public void removeFilters(@NonNull Long filterId) {
    filterPlugins.forEach(plugin ->
        plugin.removeFilters(filterId));
  }
}