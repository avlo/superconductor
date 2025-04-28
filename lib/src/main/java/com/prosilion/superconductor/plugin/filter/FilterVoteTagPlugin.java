package com.prosilion.superconductor.plugin.filter;

import nostr.event.filter.VoteTagFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.VoteTag;
import org.springframework.stereotype.Component;

@Component
public class FilterVoteTagPlugin<T extends VoteTagFilter<VoteTag>, U extends GenericEvent> extends AbstractTagFilterPlugin<T, U, VoteTag> {
  public FilterVoteTagPlugin() {
    super(VoteTagFilter.FILTER_KEY, VoteTag.class);
  }
}
