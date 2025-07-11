package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.event.SinceFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterSincePlugin extends AbstractFilterPlugin {

  public FilterSincePlugin() {
    super(SinceFilter.FILTER_KEY);
  }
}
