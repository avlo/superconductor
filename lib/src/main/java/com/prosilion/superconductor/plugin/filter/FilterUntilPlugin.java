package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.filter.event.UntilFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterUntilPlugin<T extends UntilFilter, U extends GenericEventDtoIF> extends AbstractFilterPlugin<T, U> {

  public FilterUntilPlugin() {
    super(T.FILTER_KEY);
  }
}
