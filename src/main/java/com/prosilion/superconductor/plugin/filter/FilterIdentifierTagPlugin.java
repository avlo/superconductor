package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.filter.FiltersCore;
import nostr.event.filter.IdentifierTagFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.IdentifierTag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterIdentifierTagPlugin<T extends IdentifierTagFilter<IdentifierTag>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (identifierTag, addNostrEvent) ->
        identifierTag.getPredicate().test(addNostrEvent.event());
  }

  @Override
  public List<T> getPluginFilters(FiltersCore filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return IdentifierTagFilter.filterKey;
  }
}
