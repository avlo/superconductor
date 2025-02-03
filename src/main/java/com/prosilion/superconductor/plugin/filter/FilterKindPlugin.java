package com.prosilion.superconductor.plugin.filter;

import nostr.event.Kind;
import nostr.event.filter.FiltersCore;
import nostr.event.filter.KindFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilterKindPlugin<T extends KindFilter<Kind>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public List<T> getPluginFilters(FiltersCore filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return KindFilter.filterKey;
  }
}
