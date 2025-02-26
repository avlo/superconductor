package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.GeohashTagFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.GeohashTag;
import org.springframework.stereotype.Component;

@Component
public class FilterGeohashTagPlugin<T extends GeohashTagFilter<GeohashTag>, U extends GenericEvent> extends AbstractTagFilterPlugin<T, U, GeohashTag> {
  public FilterGeohashTagPlugin() {
    super(GeohashTagFilter.FILTER_KEY, GeohashTag.class);
  }
}
