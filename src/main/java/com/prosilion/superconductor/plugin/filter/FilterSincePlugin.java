package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.SinceFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

@Component
public class FilterSincePlugin<T extends SinceFilter, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public String getCode() {
    return T.FILTER_KEY;
  }
}
