package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.base.GenericTagQuery;
import nostr.event.filter.Filters;
import nostr.event.filter.GenericTagQueryFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterGenericTagQueryPlugin<T extends GenericTagQueryFilter<GenericTagQuery>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return getBiPredicate(GenericTag.class);
  }

  @Override
  public List<T> getPluginFilters(Filters filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return "";
  }
}
