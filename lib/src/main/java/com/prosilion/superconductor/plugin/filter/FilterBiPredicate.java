package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.BaseTag;
import nostr.event.filter.Filterable;
import nostr.event.impl.GenericEvent;

import java.util.List;
import java.util.function.BiPredicate;

public interface FilterBiPredicate<T extends Filterable, U extends GenericEvent> extends FilterPlugin<T, U> {

  default <V extends BaseTag> BiPredicate<T, AddNostrEvent<U>> getBiPredicate(Class<V> clazz) {
    return (filterable, addNostrEvent) ->
        getTypeSpecificTags(clazz, addNostrEvent.event()).stream()
            .anyMatch(tag ->
                filterable.getPredicate().test(addNostrEvent.event()));
  }

  default <V extends BaseTag> List<V> getTypeSpecificTags(Class<V> tagClass, GenericEvent genericEvent) {
    return genericEvent.getTags().stream()
        .filter(tagClass::isInstance)
        .map(tagClass::cast)
        .toList();
  }
}
