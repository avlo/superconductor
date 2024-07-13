package com.prosilion.superconductor.util;

import com.prosilion.superconductor.entity.join.subscriber.AbstractFilterType;
import com.prosilion.superconductor.entity.join.subscriber.FilterPlugin;
import com.prosilion.superconductor.pubsub.AddNostrEvent;
import lombok.Getter;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

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
    System.out.println("match count: " + nostrEvents.size());

    if (nonNull(filters.getSince()) && filters.getSince() > eventToCheck.event().getCreatedAt())
      nostrEvents.add(eventToCheck);

    if (nonNull(filters.getUntil()) && filters.getUntil() <= eventToCheck.event().getCreatedAt())
      nostrEvents.add(eventToCheck);

    List<AddNostrEvent<GenericEvent>> list = nostrEvents.stream().limit(
        Optional.ofNullable(
            filters.getLimit()).orElse(100)).toList();

    list.forEach(match -> {
      System.out.println("match: " + match.event().getId() + " id");
      System.out.println("match: " + match.event().getPubKey() + " event");
    });

    return list;
  }

  private Set<AddNostrEvent<GenericEvent>> getFilterMatchingEvents(List<Combo> combos, AddNostrEvent<GenericEvent> eventToCheck) {
    return combos
        .stream()
        .map(combo ->
            filterTypeMatchesEventAttribute(
                combo.getSubscriberFilterType(),
                combo.getBiPredicate(),
                eventToCheck))
        .takeWhile(aBoolean -> aBoolean.equals(true))
        .map(result -> eventToCheck)
        .collect(Collectors.toSet());
  }

  private boolean filterTypeMatchesEventAttribute(List<?> subscriberFilters, BiPredicate biPredicate, AddNostrEvent<GenericEvent> eventToCheck) {
    return subscriberFilters
        .stream()
        .allMatch(
            testable -> biPredicate.test(testable, eventToCheck)
        );
  }
}

@Getter
class Combo {
  List<?> subscriberFilterType;
  BiPredicate<?, AddNostrEvent<GenericEvent>> biPredicate;

  public Combo(List<?> subscriberFilterType, BiPredicate<?, AddNostrEvent<GenericEvent>> biPredicate) {
    this.subscriberFilterType = subscriberFilterType;
    this.biPredicate = biPredicate;
  }
}
