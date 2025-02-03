package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.base.PublicKey;
import nostr.event.filter.FiltersCore;
import nostr.event.filter.PublicKeyFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterAuthorPlugin<T extends PublicKeyFilter<PublicKey>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (publicKeyFilter, addNostrEvent) ->
        publicKeyFilter.getPredicate().test(addNostrEvent.event());
  }

  @Override
  public List<T> getPluginFilters(FiltersCore filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return PublicKeyFilter.filterKey;
  }
}
