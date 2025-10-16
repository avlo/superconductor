package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.filter.tag.ExternalIdentityTagFilter;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import org.springframework.stereotype.Component;

@Component
public class FilterExternalIdentityTagPlugin extends AbstractTagFilterPlugin<ExternalIdentityTag> {
  public FilterExternalIdentityTagPlugin() {
    super(ExternalIdentityTagFilter.FILTER_KEY, ExternalIdentityTag.class);
  }
}
