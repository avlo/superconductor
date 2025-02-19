package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.Filters;
import nostr.event.filter.SinceFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilterSincePlugin<T extends SinceFilter, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public List<T> getPluginFilters(Filters filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return T.filterKey;
  }
}
