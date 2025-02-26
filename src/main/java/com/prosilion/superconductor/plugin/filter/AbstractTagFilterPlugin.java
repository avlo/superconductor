package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.Getter;
import lombok.NonNull;
import nostr.event.BaseTag;
import nostr.event.filter.AbstractFilterable;
import nostr.event.impl.GenericEvent;

import java.util.function.BiPredicate;

abstract class AbstractTagFilterPlugin<T extends AbstractFilterable<V>, U extends GenericEvent, V extends BaseTag> implements FilterBiPredicate<T, U> {

  @Getter
  private final String code;
  private final Class<V> tagClazz;

  protected AbstractTagFilterPlugin(@NonNull String code, @NonNull Class<V> tagClazz) {
    this.tagClazz = tagClazz;
    this.code = code;
  }

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return getBiPredicate(tagClazz);
  }
}
