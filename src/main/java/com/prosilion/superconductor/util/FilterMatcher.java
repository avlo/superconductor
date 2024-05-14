package com.prosilion.superconductor.util;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nostr.base.PublicKey;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.list.EventList;
import nostr.event.list.KindList;
import nostr.event.list.PublicKeyList;
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
  public List<AddNostrEvent<T>> addEventMatch(Filters subscriberFilters, AddNostrEvent<T> eventToCheck) {
    List<Combo<T>> combos = new ArrayList<>();

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getEvents()).orElseGet(
        () -> EventList.builder().build()
    ).getList(), eventsPredicate));

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getAuthors()).orElseGet(
        () -> PublicKeyList.builder().build()
    ).getList(), authorsPredicate));

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getKinds()).orElseGet(
        () -> KindList.builder().build()
    ).getList(), kindsPredicate));

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getReferencedEvents()).orElseGet(
        () -> EventList.builder().build()
    ).getList(), referencedEventsPredicate));

    combos.add(new Combo<>(Optional.ofNullable(
        subscriberFilters.getReferencePubKeys()).orElseGet(
        () -> PublicKeyList.builder().build()
    ).getList(), referencedPubKeysPredicate));


    Set<AddNostrEvent<T>> nostrEvents = getCollect(combos, eventToCheck);

    if (nonNull(subscriberFilters.getSince()) && subscriberFilters.getSince() > eventToCheck.event().getCreatedAt())
      nostrEvents.add(eventToCheck);

    if (nonNull(subscriberFilters.getUntil()) && subscriberFilters.getUntil() <= eventToCheck.event().getCreatedAt())
      nostrEvents.add(eventToCheck);

    return nostrEvents.stream().limit(Optional.ofNullable(subscriberFilters.getLimit()).orElse(100)).toList();
  }

  private Set<AddNostrEvent<T>> getCollect(List<Combo<T>> combos, AddNostrEvent<T> eventToCheck) {
    return combos
        .stream()
        .map(combo -> getCollect(combo.getSubscriberFilters(), combo.getBiPredicate(), eventToCheck))
        .takeWhile(aBoolean -> aBoolean.equals(true))
        .map(result -> eventToCheck)
        .collect(Collectors.toSet());
  }

  private boolean getCollect(List<?> subscriberFilters, BiPredicate biPredicate, AddNostrEvent<T> eventToCheck) {
    return subscriberFilters
        .stream()
        .allMatch(
            testable -> biPredicate.test(testable, eventToCheck));
  }

  BiPredicate<GenericEvent, AddNostrEvent<T>> eventsPredicate = (t, u) -> t.getId().equals(u.event().getId());
  BiPredicate<PublicKey, AddNostrEvent<T>> authorsPredicate = (t, u) -> t.toString().equals(u.event().getPubKey().toString());
  BiPredicate<Integer, AddNostrEvent<T>> kindsPredicate = (t, u) -> t.equals(u.event().getKind());
  BiPredicate<GenericEvent, AddNostrEvent<T>> referencedEventsPredicate = (t, u) -> t.getTags().equals(u.event().getTags());
  BiPredicate<PublicKey, AddNostrEvent<T>> referencedPubKeysPredicate = (t, u) -> t.toString().equals(u.event().getPubKey().toString());
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
