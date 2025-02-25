package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.HashtagTagFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.HashtagTag;
import org.springframework.stereotype.Component;

@Component
public class FilterHashtagTagPlugin<T extends HashtagTagFilter<HashtagTag>, U extends GenericEvent> extends AbstractTagFilterPlugin<T, U, HashtagTag> {

  public FilterHashtagTagPlugin() {
    super(HashtagTag.class);
  }

  @Override
  public String getCode() {
    return T.FILTER_KEY;
  }
}
