package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterKindRepository;
import lombok.NonNull;
import nostr.base.PublicKey;
import nostr.event.Kind;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;


@Component
public class FilterKindPlugin<
    T extends SubscriberFilterKindRepository<U>,
    U extends SubscriberFilterKind> implements FilterPlugin<T, U> {

  private final SubscriberFilterKindRepository<U> join;

  @Autowired
  public FilterKindPlugin(SubscriberFilterKindRepository<U> join) {
    this.join = join;
  }

  @Override
  public void appendFilters(@NonNull Long filterId, @NonNull Filters filters) {
    filters.setKinds(
        join.getAllByFilterId(filterId).stream().map(subscriberFilterKind ->
            Kind.valueOf(subscriberFilterKind.getKind())).toList());
  }

  @Override
  public List<U> getTypeSpecificFilterList(@NonNull Long filterId, @NonNull Filters filters) {
    return Optional.ofNullable(
            filters.getKinds())
        .orElseGet(ArrayList::new).stream().map(kind ->
            (U) new SubscriberFilterKind(filterId, kind.getValue())).toList();
  }

  @Override
  public BiPredicate<PublicKey, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.toString().equals(u.event().getPubKey().toString());
  }

  @Override
  public List<Kind> getPluginFilters(Filters filters) {
    return filters.getKinds();
  }

  @Override
  public T getJoin() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "kind";
  }
}
