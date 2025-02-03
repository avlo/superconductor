package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import nostr.base.PublicKey;
import nostr.event.filter.FiltersCore;
import nostr.event.filter.ReferencedPublicKeyFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.PubKeyTag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterReferencedPubkeyPlugin<T extends ReferencedPublicKeyFilter<PublicKey>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (referencedPublicKeyFilter, addNostrEvent) ->
        referencedPublicKeyFilter.getPredicate().test(addNostrEvent.event());
  }

  @Override
  public List<T> getPluginFilters(FiltersCore filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return ReferencedPublicKeyFilter.filterKey;
  }

  private List<PubKeyTag> getReferencedPublicKeyTags(GenericEvent genericEvent) {
    return genericEvent.getTags().stream()
        .filter(PubKeyTag.class::isInstance)
        .map(PubKeyTag.class::cast)
        .toList();
  }
}
