package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.filter.event.SinceFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterSincePlugin<T extends SinceFilter, U extends GenericEventKindIF> extends AbstractFilterPlugin<T, U> {

  public FilterSincePlugin() {
    super(T.FILTER_KEY);
  }
}
