package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.event.AuthorFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterAuthorPlugin extends AbstractFilterPlugin {
  public FilterAuthorPlugin() {
    super(AuthorFilter.FILTER_KEY);
  }
}
