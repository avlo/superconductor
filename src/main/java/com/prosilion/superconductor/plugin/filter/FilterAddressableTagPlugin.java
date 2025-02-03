package com.prosilion.superconductor.plugin.filter;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.NonNull;
import nostr.event.filter.AddressableTagFilter;
import nostr.event.filter.FiltersCore;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.AddressTag;
import nostr.event.tag.IdentifierTag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class FilterAddressableTagPlugin<T extends AddressableTagFilter<AddressTag>, U extends GenericEvent> implements FilterPlugin<T, U> {

  @Override
  public BiPredicate<T, AddNostrEvent<U>> getBiPredicate() {
    return (addressableTagFilter, addNostrEvent) ->
        compare(addressableTagFilter.getFilterCriterion(), addNostrEvent.event());
  }

  @Override
  public List<T> getPluginFilters(FiltersCore filters) {
    return getFilterableListByType(filters, getCode());
  }

  @Override
  public String getCode() {
    return AddressableTagFilter.filterKey;
  }

  private boolean compare(@NonNull AddressTag addressTag, @NonNull GenericEvent genericEvent) {
//    TODO: should be refactorable given AddressableTagFilter has same logic as below
    String genericEventPubKey = genericEvent.getPubKey().toHexString();
    Integer genericEventKind = genericEvent.getKind();
    List<IdentifierTag> genericEventIdentifierTags = getTypeSpecificTags(IdentifierTag.class, genericEvent);
    IdentifierTag addressTagIdentifierTag = addressTag.getIdentifierTag();
    String addressTagPublicKey = addressTag.getPublicKey().toHexString();
    Integer addressTagKind = addressTag.getKind();

    return genericEventPubKey.equals(addressTagPublicKey) &&
        genericEventKind.equals(addressTagKind) &&
        genericEventIdentifierTags.stream().anyMatch(identifierTag ->
            identifierTag.getId().equals(addressTagIdentifierTag.getId()));
  }
}
