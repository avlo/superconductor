package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.tag.AddressTag;
import org.springframework.stereotype.Component;

@Component
public class FilterAddressTagPlugin<T extends AddressTagFilter<AddressTag>> extends AbstractTagFilterPlugin<T, AddressTag> {
  public FilterAddressTagPlugin() {
    super(AddressTagFilter.FILTER_KEY, AddressTag.class);
  }
}
