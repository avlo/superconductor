package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import java.util.function.BiPredicate;
import org.springframework.lang.NonNull;

abstract class AbstractTagFilterPlugin<U extends BaseTag> extends AbstractFilterPlugin implements FilterBiPredicate<U> {

  private final Class<U> tagClazz;

  protected AbstractTagFilterPlugin(@NonNull String code, @NonNull Class<U> tagClazz) {
    super(code);
    this.tagClazz = tagClazz;
  }

  @Override
  public BiPredicate<Filterable, AddNostrEvent> getBiPredicate() {
    return getBiPredicate(tagClazz);
  }
}
