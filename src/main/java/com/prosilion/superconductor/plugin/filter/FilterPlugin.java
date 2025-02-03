package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.NonNull;
import lombok.SneakyThrows;
import nostr.event.filter.Filterable;
import nostr.event.filter.FiltersCore;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.tag.IdentifierTag;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface FilterPlugin<T extends Filterable, U extends GenericEvent> {

  String getCode();

//  BiPredicate<T, AddNostrEvent<U>> getBiPredicate();

  List<T> getPluginFilters(FiltersCore filters);

  default BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (contentFilter, addNostrEvent) ->
        contentFilter.getPredicate().test(addNostrEvent.event());
  }

  default List<GenericTag> getGenericQueryTags(GenericEvent genericEvent) {
    return genericEvent.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .toList();
  }

  default List<IdentifierTag> getIdentifierTags(GenericEvent genericEvent) {
    return genericEvent.getTags().stream()
        .filter(IdentifierTag.class::isInstance)
        .map(IdentifierTag.class::cast)
        .toList();
  }

  default List<T> getFilterableListByType(@NonNull FiltersCore core, @NonNull String type) {
    return Optional
        .ofNullable(
            core.getFilterableByType(type))
        .stream().flatMap(filterables ->
            filterables.stream().map(filterable ->
                (T) filterable.getFilterCriterion()))
        .toList();
  }

  @SneakyThrows
  default void setFilterableListByType(
      @NonNull FiltersCore core,
      @NonNull String key,
      @NonNull List<T> filterTypeList,
      @NonNull Function<T, Filterable> filterableFunction) {

    if (filterTypeList.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("[%s] filter must contain at least one element", key));
    }

    core.addFilterable(
        key,
        filterTypeList.stream().map(filterableFunction).collect(Collectors.toList()));
//        .orElseThrow(() ->
//            new IllegalArgumentException(
//                String.format("[%s] filter must contain at least one element")))
  }
}
