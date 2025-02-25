package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.AddressableTagFilter;
import nostr.event.tag.AddressTag;

import static nostr.event.filter.AddressableTagFilter.FILTER_KEY;

public class FilterAddressableTagPluginSuper<T extends AddressableTagFilter<AddressTag>> {
  public String getCode() {
    return T.FILTER_KEY;
  }
}
