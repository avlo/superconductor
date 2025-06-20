package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.tag.GeohashTagFilter;
import com.prosilion.nostr.tag.GeohashTag;
import org.springframework.stereotype.Component;

@Component
public class FilterGeohashTagPlugin<T extends GeohashTagFilter<GeohashTag>> extends AbstractTagFilterPlugin<T, GeohashTag> {
  public FilterGeohashTagPlugin() {
    super(GeohashTagFilter.FILTER_KEY, GeohashTag.class);
  }
}
