package com.prosilion.superconductor.base.service.request.plugin.filter;

import com.prosilion.nostr.filter.tag.RelayTagFilter;
import com.prosilion.nostr.tag.RelayTag;
import org.springframework.stereotype.Component;

@Component
public class FilterRelayTagPlugin extends AbstractTagFilterPlugin<RelayTag> {
  public FilterRelayTagPlugin() {
    super(RelayTagFilter.FILTER_KEY, RelayTag.class);
  }
}
