package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.event.KindFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterKindPlugin extends AbstractFilterPlugin {
  public FilterKindPlugin() {
    super(KindFilter.FILTER_KEY);
  }
}
