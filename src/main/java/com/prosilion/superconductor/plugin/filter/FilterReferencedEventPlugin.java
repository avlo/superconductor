package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.ReferencedEventFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.EventTag;
import org.springframework.stereotype.Component;

@Component
public class FilterReferencedEventPlugin<T extends ReferencedEventFilter<EventTag>, U extends GenericEvent> extends AbstractTagFilterPlugin<T, U, EventTag> {

  public FilterReferencedEventPlugin() {
    super(EventTag.class);
  }

  @Override
  public String getCode() {
    return T.FILTER_KEY;
  }
}
