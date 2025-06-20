package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.GenericTagQuery;
import com.prosilion.nostr.filter.tag.GenericTagQueryFilter;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import com.prosilion.nostr.event.GenericEventKindIF;
import org.springframework.stereotype.Component;

import java.util.function.BiPredicate;

@Component
public class FilterGenericTagQueryPlugin<T extends GenericTagQueryFilter<GenericTagQuery>, U extends GenericEventKindIF> implements FilterBiPredicate<T, U> {
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
