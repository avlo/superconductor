package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.AddressableTagFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.AddressTag;
import org.springframework.stereotype.Component;

@Component
public class FilterAddressableTagPlugin<T extends AddressableTagFilter<AddressTag>, U extends GenericEvent> extends AbstractTagFilterPlugin<T, U, AddressTag> {
  public FilterAddressableTagPlugin() {
    super(AddressableTagFilter.FILTER_KEY, AddressTag.class);
  }
}
