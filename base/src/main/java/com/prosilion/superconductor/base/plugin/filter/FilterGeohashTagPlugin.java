package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.filter.tag.GeohashTagFilter;
import com.prosilion.nostr.tag.GeohashTag;
import org.springframework.stereotype.Component;

@Component
public class FilterGeohashTagPlugin extends AbstractTagFilterPlugin<GeohashTag> {
  public FilterGeohashTagPlugin() {
    super(GeohashTagFilter.FILTER_KEY, GeohashTag.class);
  }
}
