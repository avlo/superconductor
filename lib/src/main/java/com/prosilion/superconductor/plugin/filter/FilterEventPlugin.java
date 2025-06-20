package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.event.EventFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterEventPlugin extends AbstractFilterPlugin {
  public FilterEventPlugin() {
    super(EventFilter.FILTER_KEY);
  }
}
