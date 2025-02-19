package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.NonNull;
import lombok.SneakyThrows;
import nostr.event.BaseTag;
import nostr.event.filter.Filterable;
import nostr.event.filter.Filters;
import nostr.event.impl.GenericEvent;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface FilterPlugin<T extends Filterable, U extends GenericEvent> {

  String getCode();
  List<T> getPluginFilters(Filters filters);

  default BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (filterable, addNostrEvent) ->
        filterable.getPredicate().test(addNostrEvent.event());
  }

  /**
   * Used exclusively by filterables requiring investigation into a *list* of an events tags:
   *    ReferencedEvent
   *    ReferencedPubkey
   *    Addressable
   *    Identifier (if more than one IdentifierTag in an event?)
   *    (likely?) GenericTagQuery
   * not:
   *    Author
   *    Event
   *    Kind
   *    Since
   *    Until
   */
  default <V extends BaseTag> BiPredicate<T, AddNostrEvent<U>> getBiPredicate(Class<V> clazz) {
    return (filterable, addNostrEvent) ->
        getTypeSpecificTags(clazz, addNostrEvent.event()).stream()
            .anyMatch(tag ->
                filterable.getPredicate().test(addNostrEvent.event()));
  }

  default <T extends BaseTag> List<T> getTypeSpecificTags(Class<T> tagClass, GenericEvent genericEvent) {
    return genericEvent.getTags().stream()
        .filter(tagClass::isInstance)
        .map(tagClass::cast)
        .toList();
  }

  default List<T> getFilterableListByType(@NonNull Filters filters, @NonNull String type) {
    return Optional
        .ofNullable(
            filters.getFilterableByType(type))
        .stream().flatMap(filterables ->
            filterables.stream().map(filterable ->
                (T) filterable.getFilterCriterion()))
        .toList();
  }

  @SneakyThrows
  default void setFilterableListByType(
      @NonNull Filters filters,
      @NonNull String key,
      @NonNull List<T> filterTypeList,
      @NonNull Function<T, Filterable> filterableFunction) {

    if (filterTypeList.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("[%s] filter must contain at least one element", key));
    }

    filters.addFilterable(
        key,
        filterTypeList.stream().map(filterableFunction).collect(Collectors.toList()));
//        .orElseThrow(() ->
//            new IllegalArgumentException(
//                String.format("[%s] filter must contain at least one element")))
  }
}
