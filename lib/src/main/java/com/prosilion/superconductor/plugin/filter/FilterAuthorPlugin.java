package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.event.GenericEventKindIF;
import org.springframework.stereotype.Component;

@Component
public class FilterAuthorPlugin<T extends AuthorFilter<V>, U extends GenericEventKindIF, V extends PublicKey> extends AbstractFilterPlugin<T, U> {
  public FilterAuthorPlugin() {
    super(T.FILTER_KEY);
  }
}
