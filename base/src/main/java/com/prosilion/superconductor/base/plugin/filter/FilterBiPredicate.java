package com.prosilion.superconductor.base.plugin.filter;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import java.util.List;
import java.util.function.BiPredicate;

public interface FilterBiPredicate<T extends BaseTag> extends FilterPlugin {

  default BiPredicate<Filterable, AddNostrEvent> getBiPredicate(Class<T> clazz) {
    return (filterable, addNostrEvent) ->
        getTypeSpecificTags(clazz, addNostrEvent.event()).stream()
            .anyMatch(tag ->
                filterable.getPredicate().test(addNostrEvent.event()));
  }

  default List<T> getTypeSpecificTags(Class<T> tagClass, GenericEventKindIF genericEvent) {
    return genericEvent.getTags().stream()
        .filter(tagClass::isInstance)
        .map(tagClass::cast)
        .toList();
  }
}
