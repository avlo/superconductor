package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.event.filter.Filterable;
import nostr.event.impl.GenericEvent;

import java.util.function.BiPredicate;

public interface FilterPlugin<T extends Filterable, U extends GenericEvent> {

  String getCode();

  default BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (filterable, addNostrEvent) ->
        filterable.getPredicate().test(addNostrEvent.event());
  }
}
