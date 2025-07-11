package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import java.util.function.BiPredicate;
import org.springframework.stereotype.Component;

@Component
public class FilterGenericTagQueryPlugin implements FilterBiPredicate<GenericTag> {
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
