package com.prosilion.superconductor.plugin.filter;

import nostr.base.PublicKey;
import nostr.event.filter.AuthorFilter;
import nostr.event.filter.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilterAuthorPlugin<T extends AuthorFilter<PublicKey>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public List<T> getPluginFilters(Filters filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return AuthorFilter.filterKey;
  }
}
