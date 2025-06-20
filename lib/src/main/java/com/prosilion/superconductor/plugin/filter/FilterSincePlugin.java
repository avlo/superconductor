package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.event.SinceFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterSincePlugin<T extends SinceFilter> extends AbstractFilterPlugin {

  public FilterSincePlugin() {
    super(T.FILTER_KEY);
  }
}
