package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.EventFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

@Component
public class FilterEventPlugin<T extends EventFilter<U>, U extends GenericEvent> extends AbstractFilterPlugin<T, U> {
  public FilterEventPlugin() {
    super(T.FILTER_KEY);
  }
}
