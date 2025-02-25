package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.BaseTag;
import nostr.event.filter.AbstractFilterable;
import nostr.event.impl.GenericEvent;

import java.util.function.BiPredicate;

abstract class AbstractTagFilterPlugin<T extends AbstractFilterable<V>, U extends GenericEvent, V extends BaseTag> implements FilterBiPredicate<T, U> {

  private final Class<V> clazz;

  protected AbstractTagFilterPlugin(Class<V> clazz) {
    this.clazz = clazz;
  }

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return getBiPredicate(clazz);
  }
}
