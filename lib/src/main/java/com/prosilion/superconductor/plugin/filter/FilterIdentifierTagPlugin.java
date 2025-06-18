package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.tag.IdentifierTag;
import org.springframework.stereotype.Component;

@Component
public class FilterIdentifierTagPlugin<T extends IdentifierTagFilter<IdentifierTag>, U extends GenericEventDtoIF> extends AbstractTagFilterPlugin<T, U, IdentifierTag> {
  public FilterIdentifierTagPlugin() {
    super(IdentifierTagFilter.FILTER_KEY, IdentifierTag.class);
  }
}
