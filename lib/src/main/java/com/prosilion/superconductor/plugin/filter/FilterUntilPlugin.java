package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.event.UntilFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterUntilPlugin<T extends UntilFilter> extends AbstractFilterPlugin {

  public FilterUntilPlugin() {
    super(T.FILTER_KEY);
  }
}
