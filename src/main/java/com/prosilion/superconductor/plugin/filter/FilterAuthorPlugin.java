package com.prosilion.superconductor.plugin.filter;

import nostr.base.PublicKey;
import nostr.event.filter.AuthorFilter;
import nostr.event.impl.GenericEvent;
import org.springframework.stereotype.Component;

@Component
public class FilterAuthorPlugin<T extends AuthorFilter<V>, U extends GenericEvent, V extends PublicKey> extends AbstractFilterPlugin<T, U> {
  public FilterAuthorPlugin() {
    super(T.FILTER_KEY);
  }
}
