package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.filter.FiltersCore;
import nostr.event.filter.SinceFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterSincePlugin<T extends SinceFilter, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (sinceFilter, addNostrEvent) ->
        sinceFilter.getPredicate().test(addNostrEvent.event());
  }

  @Override
  public List<T> getPluginFilters(FiltersCore filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return SinceFilter.filterKey;
  }
}
