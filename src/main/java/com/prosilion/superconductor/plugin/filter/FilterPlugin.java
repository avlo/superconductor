package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.entity.join.subscriber.AbstractFilterType;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;

import java.util.List;
import java.util.function.BiPredicate;

public interface FilterPlugin<T> {

  String getCode();

  BiPredicate<T, AddNostrEvent<GenericEvent>> getBiPredicate();

  List<T> getPluginFilters(Filters filters);
}
