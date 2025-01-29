package com.prosilion.superconductor.util;

import com.prosilion.superconductor.entity.join.subscriber.AbstractFilterType;
import com.prosilion.superconductor.plugin.filter.FilterPlugin;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.Getter;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

import static java.util.Objects.nonNull;

@Component
public class FilterMatcher {
  private final List<FilterPlugin<AbstractFilterType>> filterPlugins;

  @Autowired
  public FilterMatcher(List<FilterPlugin<AbstractFilterType>> filterPlugins) {
    this.filterPlugins = filterPlugins;
  }

  public List<AddNostrEvent<GenericEvent>> intersectFilterMatches(Filters filters, AddNostrEvent<GenericEvent> eventToCheck) {
    List<Combo> combos = new ArrayList<>();

    filterPlugins.forEach(filterPlugin ->
        combos.add(new Combo(Optional.ofNullable(
            filterPlugin.getPluginFilters(filters)).orElseGet(ArrayList::new),
            filterPlugin.getBiPredicate())));

    Set<AddNostrEvent<GenericEvent>> nostrEvents = getFilterMatchingEvents(combos, eventToCheck);

    if (withinRange(filters.getSince(), filters.getUntil(), eventToCheck.event().getCreatedAt())) {
      nostrEvents.add(eventToCheck);
    }

    return nostrEvents.stream().limit(
        Optional.ofNullable(
            filters.getLimit()).orElse(10)).toList();
  }

  private boolean withinRange(Long since, Long until, Long createdAt) {
    if (!nonNull(since) && !nonNull(until))
      return false;
    if ((nonNull(since) && !nonNull(until)) && (since < createdAt))
      return true;
    if ((!nonNull(since) && (until >= createdAt)))
      return true;
    if (nonNull(since) && nonNull(until)) {
      return ((since < createdAt) && (until >= createdAt));
    }
    return false;
  }

  private <U> Set<AddNostrEvent<GenericEvent>> getFilterMatchingEvents(List<Combo> combos, AddNostrEvent<GenericEvent> eventToCheck) {
//    TODO: convert to stream
    Set<AddNostrEvent<GenericEvent>> matchedCombos = new HashSet<>();
    for (Combo<U> combo : combos) {
      if (filterTypeMatchesEventAttribute(combo, eventToCheck)) {
        matchedCombos.add(eventToCheck);
      }
    }
    return matchedCombos;
  }

  private <U> Set<AddNostrEvent<GenericEvent>> getFilterMatchingEventsRxR(List<Combo> combos, AddNostrEvent<GenericEvent> eventToCheck) {
//    TODO: convert to stream
    Set<AddNostrEvent<GenericEvent>> matchedCombos = new HashSet<>();
    combos.stream().filter(combo -> filterTypeMatchesEventAttribute(combo, eventToCheck)).forEach(combo -> matchedCombos.add(eventToCheck));
    return matchedCombos;
  }

  private <U> boolean filterTypeMatchesEventAttribute(Combo<U> combo, AddNostrEvent<GenericEvent> eventToCheck) {
//    TODO: convert to stream
    for (U testable : combo.getSubscriberFilterType()) {
      if (combo.getBiPredicate().test(testable, eventToCheck))
        return true;
    }
    return false;
  }

  private <U> boolean filterTypeMatchesEventAttributeRxR(Combo<U> combo, AddNostrEvent<GenericEvent> eventToCheck) {
    boolean anyMatch = combo.getSubscriberFilterType().stream().allMatch(testable -> combo.getBiPredicate().test(testable, eventToCheck));
    return anyMatch;
  }
}

@Getter
class Combo<U> {
  List<U> subscriberFilterType;
  BiPredicate<U, AddNostrEvent<GenericEvent>> biPredicate;

  public Combo(List<U> subscriberFilterType, BiPredicate<U, AddNostrEvent<GenericEvent>> biPredicate) {
    this.subscriberFilterType = subscriberFilterType;
    this.biPredicate = biPredicate;
  }
}
