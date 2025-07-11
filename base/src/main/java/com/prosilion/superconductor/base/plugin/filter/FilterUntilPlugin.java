package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.filter.event.UntilFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterUntilPlugin extends AbstractFilterPlugin {

  public FilterUntilPlugin() {
    super(UntilFilter.FILTER_KEY);
  }
}
