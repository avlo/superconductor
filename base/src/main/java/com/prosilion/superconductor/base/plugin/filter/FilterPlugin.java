package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.filter.Filterable;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import java.util.function.BiPredicate;

public interface FilterPlugin {

  String getCode();

  default BiPredicate<Filterable, AddNostrEvent> getBiPredicate() {
    return (filterable, addNostrEvent) ->
        filterable.getPredicate().test(addNostrEvent.event());
  }
}
