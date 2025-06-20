package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.tag.HashtagTagFilter;
import com.prosilion.nostr.tag.HashtagTag;
import org.springframework.stereotype.Component;

@Component
public class FilterHashtagTagPlugin extends AbstractTagFilterPlugin<HashtagTag> {
  public FilterHashtagTagPlugin() {
    super(HashtagTagFilter.FILTER_KEY, HashtagTag.class);
  }
}
