package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.EventFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

@Component
public class FilterEventPlugin<T extends EventFilter<U>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public String getCode() {
    return T.FILTER_KEY;
  }
}
