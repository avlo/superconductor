package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.filter.GenericTagQuery;
import com.prosilion.nostr.filter.tag.GenericTagQueryFilter;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import java.util.function.BiPredicate;
import org.springframework.stereotype.Component;

@Component
public class FilterGenericTagQueryPlugin<T extends GenericTagQueryFilter<GenericTagQuery>> implements FilterBiPredicate<GenericTag> {
  public static final String BLANK = "";

  @Override
  public BiPredicate<Filterable, AddNostrEvent> getBiPredicate() {
    return getBiPredicate(GenericTag.class);
  }

  @Override
  public String getCode() {
    return BLANK;
  }
}
