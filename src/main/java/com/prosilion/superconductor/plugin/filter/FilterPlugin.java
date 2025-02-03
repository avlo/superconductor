package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.NonNull;
import lombok.SneakyThrows;
import nostr.event.BaseTag;
import nostr.event.filter.Filterable;
import nostr.event.filter.FiltersCore;
import nostr.event.impl.GenericEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface FilterPlugin<T extends Filterable, U extends GenericEvent> {

  String getCode();

  List<T> getPluginFilters(FiltersCore filters);

  default BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (filterable, addNostrEvent) ->
        filterable.getPredicate().test(addNostrEvent.event());
  }

  default <T extends BaseTag> List<T> getTypeSpecificTags(Class<T> tagClass, GenericEvent genericEvent) {
    return genericEvent.getTags().stream()
        .filter(tagClass::isInstance)
        .map(tagClass::cast)
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
