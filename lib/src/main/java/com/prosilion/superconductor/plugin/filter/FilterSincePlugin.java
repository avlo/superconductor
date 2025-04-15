package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.SinceFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

@Component
public class FilterSincePlugin<T extends SinceFilter, U extends GenericEvent> extends AbstractFilterPlugin<T, U> {

  public FilterSincePlugin() {
    super(T.FILTER_KEY);
  }
}
