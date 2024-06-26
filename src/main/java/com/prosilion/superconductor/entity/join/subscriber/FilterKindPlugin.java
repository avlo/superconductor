package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterKindRepository;
import lombok.NonNull;
import nostr.event.Kind;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
  public T getJoin() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "kind";
  }
}
