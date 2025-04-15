package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.UntilFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

@Component
public class FilterUntilPlugin<T extends UntilFilter, U extends GenericEvent> extends AbstractFilterPlugin<T, U> {

  public FilterUntilPlugin() {
    super(T.FILTER_KEY);
  }
}
