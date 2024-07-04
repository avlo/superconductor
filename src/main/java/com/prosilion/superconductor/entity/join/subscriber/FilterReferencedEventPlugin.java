package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterReferencedEventRepository;
import lombok.NonNull;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

@Component
public class FilterReferencedEventPlugin<
    T extends SubscriberFilterReferencedEventRepository<U>,
    U extends SubscriberFilterReferencedEvent> implements FilterPlugin<T, U> {

  private final SubscriberFilterReferencedEventRepository<U> join;

  @Autowired
  public FilterReferencedEventPlugin(SubscriberFilterReferencedEventRepository<U> join) {
    this.join = join;
  }

  @Override
  public void appendFilters(@NonNull Long filterId, @NonNull Filters filters) {
    filters.setReferencedEvents(
        join.getAllByFilterId(filterId).stream().map(referencedEvent ->
            new GenericEvent(referencedEvent.getReferencedEventId())).toList());
  }

  @Override
  public List<U> getTypeSpecificFilterList(@NonNull Long filterId, @NonNull Filters filters) {
    return Optional.ofNullable(
            filters.getReferencedEvents())
        .orElseGet(ArrayList::new).stream().map(genericEvent ->
            (U) new SubscriberFilterReferencedEvent(filterId, genericEvent.getId())).toList();
  }

  @Override
  public BiPredicate<GenericEvent, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.getId().equals(u.event().getId());
  }

  @Override
  public List<GenericEvent> getPluginFilters(Filters filters) {
    return filters.getReferencedEvents();
  }

  @Override
  public T getJoin() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "referencedEvent";
  }
}
