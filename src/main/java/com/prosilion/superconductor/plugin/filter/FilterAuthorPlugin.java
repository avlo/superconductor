package com.prosilion.superconductor.plugin.filter;

import nostr.base.PublicKey;
import nostr.event.filter.FiltersCore;
import nostr.event.filter.PublicKeyFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilterAuthorPlugin<T extends PublicKeyFilter<PublicKey>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public List<T> getPluginFilters(FiltersCore filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return PublicKeyFilter.filterKey;
  }
}
