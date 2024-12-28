package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.BaseTag;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;

import java.util.List;
import java.util.function.BiPredicate;

public interface FilterPlugin<T> {

  String getCode();

  BiPredicate<T, AddNostrEvent<GenericEvent>> getBiPredicate();

  List<T> getPluginFilters(Filters filters);

  default List<GenericTag> getGenericQueryTags(List<BaseTag> baseTags) {
    List<GenericTag> list = baseTags.stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .toList();
    return list;
  }
}
