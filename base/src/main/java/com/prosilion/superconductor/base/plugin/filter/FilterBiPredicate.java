package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import java.util.function.BiPredicate;

public interface FilterBiPredicate<T extends BaseTag> extends FilterPlugin {

  default BiPredicate<Filterable, AddNostrEvent> getBiPredicate(Class<T> clazz) {
    return (filterable, addNostrEvent) ->
        Filterable.getTypeSpecificTagsStream(clazz, addNostrEvent.event())
            .anyMatch(tag ->
                filterable.getPredicate().test(addNostrEvent.event()));
  }
}
