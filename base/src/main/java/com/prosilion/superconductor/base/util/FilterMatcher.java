package com.prosilion.superconductor.base.util;

import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.superconductor.base.plugin.filter.FilterPlugin;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterMatcher {
  private final Map<String, FilterPlugin> filterPluginsMap = new HashMap<>();

  @Autowired
  public FilterMatcher(List<FilterPlugin> filterPlugins) {
    filterPlugins.forEach(filterPlugin ->
        filterPluginsMap.put(filterPlugin.getCode(), filterPlugin));
  }

  public Optional<AddNostrEvent> intersectFilterMatches(Filters requestFilters, AddNostrEvent eventToCheck) {
    List<Combo<Filterable>> combos = new ArrayList<>();

    requestFilters.getFiltersMap().forEach((key, value) ->
    {
      FilterPlugin filterPlugin = filterPluginsMap.getOrDefault(key, filterPluginsMap.get(""));
      combos.add(
          new Combo<>(
              value,
              filterPlugin.getBiPredicate()));
    });

    return getFilterMatchingEvents(combos, eventToCheck) ? Optional.of(eventToCheck) : Optional.empty();
  }

  private <U extends Filterable> boolean getFilterMatchingEvents(List<Combo<U>> combos, AddNostrEvent eventToCheck) {
    for (Combo<U> combo : combos) {
      if (!filterableMatchesAtLeastOneEventAttribute(combo, eventToCheck)) {
        return false;
      }
    }
    return true;
  }

  private <U extends Filterable> boolean filterableMatchesAtLeastOneEventAttribute(Combo<U> combo, AddNostrEvent eventToCheck) {
//    TODO: convert to stream
    List<U> subscriberFilterType = combo.getFilterable();
    for (U testable : subscriberFilterType) {
      BiPredicate<U, AddNostrEvent> biPredicate = combo.getBiPredicate();
      if (biPredicate.test(testable, eventToCheck))
        return true;
    }
    return false;
  }

  @Getter
  class Combo<V extends Filterable> {
    List<V> filterable;
    BiPredicate<V, AddNostrEvent> biPredicate;

    public Combo(List<V> filterable, BiPredicate<V, AddNostrEvent> biPredicate) {
      this.filterable = filterable;
      this.biPredicate = biPredicate;
    }
  }
}
