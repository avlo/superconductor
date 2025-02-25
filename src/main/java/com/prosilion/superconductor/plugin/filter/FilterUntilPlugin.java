package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.UntilFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

@Component
public class FilterUntilPlugin<T extends UntilFilter, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public String getCode() {
    return T.FILTER_KEY;
  }
}
