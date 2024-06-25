package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.repository.join.subscriber.SubscriberFilterReferencedEventRepository;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class FilterReferencedEventPlugin implements FilterPlugin {

  private final SubscriberFilterReferencedEventRepository<SubscriberFilterReferencedEvent> join;

  @Autowired
  public FilterReferencedEventPlugin(SubscriberFilterReferencedEventRepository<SubscriberFilterReferencedEvent> join) {
    this.join = join;
  }

  @Override
  public void appendFilters(Long filterId, Filters filters) {
    filters.setReferencedEvents(
        join.getAllByFilterId(filterId).stream().map(referencedEvent ->
            new GenericEvent(referencedEvent.getReferencedEventId())).toList());
  }

  @Override
  public void saveFilter(Long filterId, Filters filters) {
    join.saveAllAndFlush(() ->
        Optional.ofNullable(
                filters.getReferencedEvents())
            .orElseGet(ArrayList::new).stream().map(genericEvent ->
                new SubscriberFilterReferencedEvent(filterId, genericEvent.getId())).toList().iterator());
  }

  @Override
  public SubscriberFilterReferencedEventRepository getJoin() {
    return join;
  }

  @Override
  public String getCode() {
    return "referencedEvent";
  }
}
