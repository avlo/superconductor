package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterReferencedEventPlugin<T extends SubscriberFilterReferencedEvent> implements FilterPlugin<T> {

  @Override
  public BiPredicate<GenericEvent, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.getId().equals(u.event().getId());
  }

  @Override
  public List<GenericEvent> getPluginFilters(Filters filters) {
    return filters.getReferencedEvents();
  }

  @Override
  public String getCode() {
    return "referencedEvent";
  }
}
