package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.EventFilter;
import nostr.event.filter.FiltersCore;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilterEventPlugin<T extends EventFilter<GenericEvent>, U extends GenericEvent> implements FilterPlugin<T, U> {

//  @Override
//  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
//    return (eventFilter, addNostrEvent) ->
//        eventFilter.getPredicate().test(addNostrEvent.event());
//  }

  @Override
  public List<T> getPluginFilters(FiltersCore filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return EventFilter.filterKey;
  }
}
