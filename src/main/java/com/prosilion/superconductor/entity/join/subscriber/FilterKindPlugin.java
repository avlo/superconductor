package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterKindRepository;
import nostr.event.Kind;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;


@Component
public class FilterKindPlugin implements FilterPlugin {

  private final SubscriberFilterKindRepository<SubscriberFilterKind> join;

  @Autowired
  public FilterKindPlugin(SubscriberFilterKindRepository<SubscriberFilterKind> join) {
    this.join = join;
  }

  @Override
  public void appendFilters(Long filterId, Filters filters) {
    filters.setKinds(
        join.getAllByFilterId(filterId).stream().map(subscriberFilterKind ->
            Kind.valueOf(subscriberFilterKind.getKind())).toList());
  }

  @Override
  public void saveFilter(Long filterId, Filters filters) {
    join.saveAllAndFlush(() ->
        Optional.ofNullable(
                filters.getKinds())
            .orElseGet(ArrayList::new).stream().map(kind ->
                new SubscriberFilterKind(filterId, kind.getValue())).toList().iterator());
  }

  @Override
  public SubscriberFilterKindRepository getJoin() {
    return join;
  }

  @Override
  public String getCode() {
    return "kind";
  }
}
