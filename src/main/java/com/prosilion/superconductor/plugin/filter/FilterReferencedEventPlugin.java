package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.filter.FiltersCore;
import nostr.event.filter.ReferencedEventFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.EventTag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterReferencedEventPlugin<T extends ReferencedEventFilter<GenericEvent>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (referencedEventFilter, addNostrEvent) ->
        getTypeSpecificTags(EventTag.class, addNostrEvent.event()).stream().anyMatch(eventTag ->
            eventTag.getIdEvent().equals(referencedEventFilter.getFilterCriterion().getId()));
  }

  @Override
  public List<T> getPluginFilters(FiltersCore filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return ReferencedEventFilter.filterKey;
  }
}
