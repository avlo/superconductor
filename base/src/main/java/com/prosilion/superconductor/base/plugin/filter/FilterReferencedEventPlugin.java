package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.filter.tag.ReferencedEventFilter;
import com.prosilion.nostr.tag.EventTag;
import org.springframework.stereotype.Component;

@Component
public class FilterReferencedEventPlugin extends AbstractTagFilterPlugin<EventTag> {
  public FilterReferencedEventPlugin() {
    super(ReferencedEventFilter.FILTER_KEY, EventTag.class);
  }
}
