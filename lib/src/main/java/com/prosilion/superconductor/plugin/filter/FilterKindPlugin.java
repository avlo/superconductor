package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.filter.event.KindFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterKindPlugin<T extends KindFilter<Kind>, U extends GenericEventKindIF> extends AbstractFilterPlugin<T, U> {
  public FilterKindPlugin() {
    super(T.FILTER_KEY);
  }
}
