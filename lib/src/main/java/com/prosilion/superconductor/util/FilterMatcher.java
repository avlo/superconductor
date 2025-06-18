package com.prosilion.superconductor.util;

import com.prosilion.superconductor.plugin.filter.FilterPlugin;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.Getter;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.event.GenericEventDtoIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

@Component
public class FilterMatcher<T extends GenericEventDtoIF> {
  private final Map<String, FilterPlugin<Filterable, T>> filterPluginsMap = new HashMap<>();

  @Autowired
  public FilterMatcher(List<FilterPlugin<Filterable, T>> filterPlugins) {
    filterPlugins.forEach(filterPlugin ->
        filterPluginsMap.put(filterPlugin.getCode(), filterPlugin));
  }

  public Optional<AddNostrEvent<T>> intersectFilterMatches(Filters requestFilters, AddNostrEvent<T> eventToCheck) {
    List<Combo<Filterable>> combos = new ArrayList<>();

    requestFilters.getFiltersMap().forEach((key, value) ->
    {
      FilterPlugin<Filterable, T> filterPlugin = filterPluginsMap.getOrDefault(key, filterPluginsMap.get(""));
      combos.add(
          new Combo<>(
              value,
              filterPlugin.getBiPredicate()));
    });

    return getFilterMatchingEvents(combos, eventToCheck) ? Optional.of(eventToCheck) : Optional.empty();
  }

  private <U extends Filterable> boolean getFilterMatchingEvents(List<Combo<U>> combos, AddNostrEvent<T> eventToCheck) {
    for (Combo<U> combo : combos) {
      if (!filterableMatchesAtLeastOneEventAttribute(combo, eventToCheck)) {
        return false;
      }
    }
    return true;
  }

  private <U extends Filterable> boolean filterableMatchesAtLeastOneEventAttribute(Combo<U> combo, AddNostrEvent<T> eventToCheck) {
//    TODO: convert to stream
    List<U> subscriberFilterType = combo.getFilterable();
    for (U testable : subscriberFilterType) {
      BiPredicate<U, AddNostrEvent<T>> biPredicate = combo.getBiPredicate();
      if (biPredicate.test(testable, eventToCheck))
        return true;
    }
    return false;
  }

  @Getter
  class Combo<V extends Filterable> {
    List<V> filterable;
    BiPredicate<V, AddNostrEvent<T>> biPredicate;

    public Combo(List<V> filterable, BiPredicate<V, AddNostrEvent<T>> biPredicate) {
      this.filterable = filterable;
      this.biPredicate = biPredicate;
    }
  }
}
