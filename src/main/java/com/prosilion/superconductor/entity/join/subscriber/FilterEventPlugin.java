package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterEventRepository;
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
public class FilterEventPlugin<
    T extends SubscriberFilterEventRepository<U>,
    U extends SubscriberFilterEvent> implements FilterPlugin<T, U> {

  private final SubscriberFilterEventRepository<U> join;

  @Autowired
  public FilterEventPlugin(SubscriberFilterEventRepository<U> join) {
    this.join = join;
  }

  @Override
  public void appendFilters(@NonNull Long filterId, @NonNull Filters filters) {
    filters.setEvents(
        join.getAllByFilterId(filterId).stream().map(event ->
            new GenericEvent(event.getEventIdString())).toList());
  }

  @Override
  public List<U> getTypeSpecificFilterList(@NonNull Long filterId, @NonNull Filters filters) {
    return Optional.ofNullable(
            filters.getEvents())
        .orElseGet(ArrayList::new).stream().map(genericEvent ->
            (U) new SubscriberFilterEvent(filterId, genericEvent.getId())).toList();
  }

  @Override
  public BiPredicate<GenericEvent, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.getId().equals(u.event().getId());
  }

  @Override
  public List<GenericEvent> getPluginFilters(Filters filters) {
    return filters.getEvents();
  }

  @Override
  public T getJoin() {
    return (T) join;
  }

  @Override
  public String getCode() {
    return "event";
  }
}
