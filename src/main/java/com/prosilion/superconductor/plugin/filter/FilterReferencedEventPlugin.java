package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.EventTag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterReferencedEventPlugin<T extends GenericEvent> implements FilterPlugin<T> {

  @Override
  public BiPredicate<T, AddNostrEvent<GenericEvent>> getBiPredicate() {
    return (t, u) ->
        u.event().getTags().stream()
            .filter(EventTag.class::isInstance)
            .map(EventTag.class::cast)
            .anyMatch(eventTag -> eventTag.getIdEvent().equals(t.getId()));
  }

  @Override
  public List<T> getPluginFilters(Filters filters) {
    return (List<T>) filters.getReferencedEvents();
  }

  @Override
  public String getCode() {
    return "referencedEvent";
  }
}
