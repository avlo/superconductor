package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.event.GenericEventDtoIF;
import org.springframework.stereotype.Component;

@Component
public class FilterEventPlugin<T extends EventFilter<GenericEventId>, U extends GenericEventDtoIF> extends AbstractFilterPlugin<T, U> {
  public FilterEventPlugin() {
    super(T.FILTER_KEY);
  }
}
