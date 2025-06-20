package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.tag.ReferencedEventFilter;
import com.prosilion.nostr.tag.EventTag;
import org.springframework.stereotype.Component;

@Component
public class FilterReferencedEventPlugin<T extends ReferencedEventFilter<EventTag>> extends AbstractTagFilterPlugin<T, EventTag> {
  public FilterReferencedEventPlugin() {
    super(ReferencedEventFilter.FILTER_KEY, EventTag.class);
  }
}
