package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.tag.IdentifierTag;
import org.springframework.stereotype.Component;

@Component
public class FilterIdentifierTagPlugin extends AbstractTagFilterPlugin<IdentifierTag> {
  public FilterIdentifierTagPlugin() {
    super(IdentifierTagFilter.FILTER_KEY, IdentifierTag.class);
  }
}
