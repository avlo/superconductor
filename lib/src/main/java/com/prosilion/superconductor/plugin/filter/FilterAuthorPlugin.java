package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.user.PublicKey;
import org.springframework.stereotype.Component;

@Component
public class FilterAuthorPlugin<T extends AuthorFilter<U>, U extends PublicKey> extends AbstractFilterPlugin {
  public FilterAuthorPlugin() {
    super(T.FILTER_KEY);
  }
}
