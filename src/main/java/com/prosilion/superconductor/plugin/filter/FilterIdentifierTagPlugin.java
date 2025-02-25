package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.IdentifierTagFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.IdentifierTag;
import org.springframework.stereotype.Component;

@Component
public class FilterIdentifierTagPlugin<T extends IdentifierTagFilter<IdentifierTag>, U extends GenericEvent> extends AbstractTagFilterPlugin<T, U, IdentifierTag> {

  public FilterIdentifierTagPlugin() {
    super(IdentifierTag.class);
  }

  @Override
  public String getCode() {
    return T.FILTER_KEY;
  }
}
