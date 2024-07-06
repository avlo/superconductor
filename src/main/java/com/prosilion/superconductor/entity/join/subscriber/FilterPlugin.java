package com.prosilion.superconductor.entity.join.subscriber;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;

import java.util.List;
import java.util.function.BiPredicate;

public interface FilterPlugin<T extends AbstractFilterType> {

  String getCode();

  BiPredicate<?, AddNostrEvent<GenericEvent>> getBiPredicate();

  List<?> getPluginFilters(Filters filters);
}
