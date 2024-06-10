package com.prosilion.superconductor.util;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nostr.base.PublicKey;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@NoArgsConstructor
public class FilterMatcher<T extends GenericEvent> {
  BiPredicate<GenericEvent, AddNostrEvent<T>> eventsPredicate = (t, u) -> t.getId().equals(u.event().getId());
  BiPredicate<PublicKey, AddNostrEvent<T>> authorsPredicate = (t, u) -> t.toString().equals(u.event().getPubKey().toString());
  BiPredicate<Integer, AddNostrEvent<T>> kindsPredicate = (t, u) -> t.equals(u.event().getKind());
  BiPredicate<GenericEvent, AddNostrEvent<T>> referencedEventsPredicate = (t, u) -> t.getTags().equals(u.event().getTags());
  BiPredicate<PublicKey, AddNostrEvent<T>> referencedPubKeysPredicate = (t, u) -> t.toString().equals(u.event().getPubKey().toString());

  public List<AddNostrEvent<T>> intersectFilterMatches(Filters subscriberFilters, AddNostrEvent<T> eventToCheck) {
    List<Combo<T>> combos = new ArrayList<>();

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getEvents()).orElseGet(ArrayList::new
    ), eventsPredicate));

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getAuthors()).orElseGet(ArrayList::new
    ), authorsPredicate));

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getKinds()).orElseGet(ArrayList::new
    ), kindsPredicate));

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getReferencedEvents()).orElseGet(ArrayList::new
    ), referencedEventsPredicate));

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getReferencePubKeys()).orElseGet(ArrayList::new
    ), referencedPubKeysPredicate));


    Set<AddNostrEvent<T>> nostrEvents = getCollect(combos, eventToCheck);

    if (nonNull(subscriberFilters.getSince()) && subscriberFilters.getSince() > eventToCheck.event().getCreatedAt())
      nostrEvents.add(eventToCheck);

    if (nonNull(subscriberFilters.getUntil()) && subscriberFilters.getUntil() <= eventToCheck.event().getCreatedAt())
      nostrEvents.add(eventToCheck);

    return nostrEvents.parallelStream().limit(Optional.ofNullable(subscriberFilters.getLimit()).orElse(100)).toList();
  }

  private Set<AddNostrEvent<T>> getCollect(List<Combo<T>> combos, AddNostrEvent<T> eventToCheck) {
    return combos
        .parallelStream()
        .map(combo -> getCollect(combo.getSubscriberFilters(), combo.getBiPredicate(), eventToCheck))
        .takeWhile(aBoolean -> aBoolean.equals(true))
        .map(result -> eventToCheck)
        .collect(Collectors.toSet());
  }

//  TOOD: refactor
  private boolean getCollect(List<?> subscriberFilters, BiPredicate biPredicate, AddNostrEvent<T> eventToCheck) {
    return subscriberFilters
        .parallelStream()
        .allMatch(
            testable -> biPredicate.test(testable, eventToCheck));
  }

}

@Getter
class Combo<T extends GenericEvent> {
  List<?> subscriberFilters;
  BiPredicate<?, AddNostrEvent<T>> biPredicate;

  public Combo(List<?> subscriberFilters, BiPredicate<?, AddNostrEvent<T>> biPredicate) {
    this.subscriberFilters = subscriberFilters;
    this.biPredicate = biPredicate;
  }
}
