package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.entity.join.subscriber.SubscriberFilterEvent;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterEventPlugin<T extends SubscriberFilterEvent> implements FilterPlugin<T> {

  @Override
  public BiPredicate<GenericEvent, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) -> t.getId().equals(u.event().getId());
  }

  @Override
  public List<GenericEvent> getPluginFilters(Filters filters) {
    return filters.getEvents();
  }

  @Override
  public String getCode() {
    return "event";
  }
}
