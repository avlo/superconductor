package com.prosilion.superconductor.plugin.filter;

import com.prosilion.nostr.filter.AbstractFilterable;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventDtoIF;

import java.util.function.BiPredicate;

abstract class AbstractTagFilterPlugin<T extends AbstractFilterable<V>, U extends GenericEventDtoIF, V extends BaseTag> extends AbstractFilterPlugin<T, U> implements FilterBiPredicate<T, U> {

  private final Class<V> tagClazz;

  protected AbstractTagFilterPlugin(@NonNull String code, @NonNull Class<V> tagClazz) {
    super(code);
    this.tagClazz = tagClazz;
  }

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return getBiPredicate(tagClazz);
  }
}
