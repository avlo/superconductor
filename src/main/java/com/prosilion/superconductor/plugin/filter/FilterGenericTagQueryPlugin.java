package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.base.GenericTagQuery;
import nostr.event.filter.GenericTagQueryFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import org.springframework.stereotype.Component;

import java.util.function.BiPredicate;

@Component
public class FilterGenericTagQueryPlugin<T extends GenericTagQueryFilter<GenericTagQuery>, U extends GenericEvent> implements FilterBiPredicate<T, U> {
  public static final String BLANK = "";

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return getBiPredicate(GenericTag.class);
  }

  @Override
  public String getCode() {
    return BLANK;
  }
}
