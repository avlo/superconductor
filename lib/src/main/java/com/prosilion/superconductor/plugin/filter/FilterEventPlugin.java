package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.filter.event.EventFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterEventPlugin<T extends EventFilter<GenericEventId>> extends AbstractFilterPlugin {
  public FilterEventPlugin() {
    super(T.FILTER_KEY);
  }
}
