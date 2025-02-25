package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.NonNull;
import nostr.event.filter.Filterable;
import nostr.event.filter.Filters;
import nostr.event.impl.GenericEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

public interface FilterPlugin<T extends Filterable, U extends GenericEvent> {

  String getCode();

  default List<T> getPluginFilters(Filters filters) {
    return getFilterableListByType(filters, getCode());
  }

  default BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (filterable, addNostrEvent) ->
        filterable.getPredicate().test(addNostrEvent.event());
  }

  default List<T> getFilterableListByType(@NonNull Filters filters, @NonNull String type) {
    return Optional
        .ofNullable(
            filters.getFilterByType(type))
        .stream().flatMap(filterables ->
            filterables.stream().map(filterable ->
                (T) filterable.getFilterable()))
        .toList();
  }
}
