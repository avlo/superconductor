package com.prosilion.superconductor.util;

import com.prosilion.superconductor.plugin.filter.FilterPlugin;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.Getter;
import nostr.event.filter.Filterable;
import nostr.event.filter.FiltersCore;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class FilterMatcher<T extends GenericEvent> {
  private final Map<String, FilterPlugin<Filterable, T>> filterPluginsMap = new HashMap<>();

  @Autowired
  public FilterMatcher(List<FilterPlugin<Filterable, T>> filterPlugins) {
    filterPlugins.forEach(filterPlugin ->
    {
      String code = filterPlugin.getCode();
      filterPluginsMap.put(code, filterPlugin);
    });
  }

  public Optional<AddNostrEvent<T>> intersectFilterMatches(FiltersCore userFiltersCore, AddNostrEvent<T> eventToCheck) {
    List<Combo<Filterable>> combos = new ArrayList<>();

    userFiltersCore.getFiltersMap().forEach((key, value) ->
    {
      FilterPlugin<Filterable, T> filterableTFilterPlugin = filterPluginsMap.get(key);
      combos.add(
          new Combo<>(
              value,
              filterableTFilterPlugin.getBiPredicate()));
    });

    boolean filterMatchingEvents = getFilterMatchingEvents(combos, eventToCheck);
    if (filterMatchingEvents
//        && withinRange(
//            userFiltersCore.getFilterableByType(SinceFilter.filterKey).getFirst().getFilterCriterion(),
//            userFiltersCore.getFilterableByType(UntilFilter.filterKey).getFirst().getFilterCriterion(),
//            eventToCheck.event().getCreatedAt())
    ) {
      return Optional.of(eventToCheck);
    }
    return Optional.empty();
  }

  private boolean withinRange(Long since, Long until, Long createdAt) {
    if (isNull(since) && isNull(until))
      return true;
    if ((nonNull(since) && isNull(until)) && (since < createdAt))
      return true;
    if ((isNull(since) && (until >= createdAt)))
      return true;
    if (nonNull(since) && nonNull(until)) {
      return ((since < createdAt) && (until >= createdAt));
    }
    return false;
  }

  private <U> boolean getFilterMatchingEvents(List<Combo<U>> combos, AddNostrEvent<T> eventToCheck) {
    for (Combo<U> combo : combos) {
      if (!filterableMatchesEventAttribute(combo, eventToCheck)) {
        return false;
      }
    }
    return true;
  }

  private <U> boolean filterableMatchesEventAttribute(Combo<U> combo, AddNostrEvent<T> eventToCheck) {
//    TODO: convert to stream
    List<U> subscriberFilterType = combo.getFilterable();
    for (U testable : subscriberFilterType) {
      BiPredicate<U, AddNostrEvent<T>> biPredicate = combo.getBiPredicate();
      if (!biPredicate.test(testable, eventToCheck))
        return false;
    }
    return true;
  }

  @Getter
  class Combo<Filterable> {
    List<Filterable> filterable;
    BiPredicate<Filterable, AddNostrEvent<T>> biPredicate;

    public Combo(List<Filterable> filterable, BiPredicate<Filterable, AddNostrEvent<T>> biPredicate) {
      this.filterable = filterable;
      this.biPredicate = biPredicate;
    }
  }
}
