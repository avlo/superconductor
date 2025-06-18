package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.filter.event.SinceFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterSincePlugin<T extends SinceFilter, U extends GenericEventDtoIF> extends AbstractFilterPlugin<T, U> {

  public FilterSincePlugin() {
    super(T.FILTER_KEY);
  }
}
