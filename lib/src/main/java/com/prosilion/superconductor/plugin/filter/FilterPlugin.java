package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.event.GenericEventDtoIF;

import java.util.function.BiPredicate;

public interface FilterPlugin<T extends Filterable, U extends GenericEventDtoIF> {

  String getCode();

  default BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (filterable, addNostrEvent) ->
        filterable.getPredicate().test(addNostrEvent.event());
  }
}
