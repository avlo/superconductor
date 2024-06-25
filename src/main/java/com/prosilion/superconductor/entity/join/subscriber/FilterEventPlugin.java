package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterEventRepository;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class FilterEventPlugin implements FilterPlugin {

  private final SubscriberFilterEventRepository<SubscriberFilterEvent> join;

  @Autowired
  public FilterEventPlugin(SubscriberFilterEventRepository<SubscriberFilterEvent> join) {
    this.join = join;
  }

  @Override
  public Filters appendFilters(Long filterId, Filters filters) {
    filters.setEvents(
        join.getAllByFilterId(filterId).stream().map(event ->
            new GenericEvent(event.getEventIdString())).toList());
    return filters;
  }

  @Override
  public void saveFilter(Long filterId, Filters filters) {
// TODO: saveAllAndFlush() vs save(), possibly solves inconsistency issues w/ entityManager?
    join.saveAllAndFlush(() ->
        Optional.ofNullable(
                filters.getEvents())
            .orElseGet(ArrayList::new).stream().map(genericEvent ->
                new SubscriberFilterEvent(filterId, genericEvent.getId())).toList().iterator());
  }

  @Override
  public SubscriberFilterEventRepository getJoin() {
    return join;
  }

  @Override
  public String getCode() {
    return "event";
  }
}
