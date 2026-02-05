package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;

// TODO: future possibly integrate with CacheBadgeDefinitionReputationEventServiceIF
//       requires potential refactor between AddressableEvent and TagMappedEventIF
public interface CacheBadgeDefinitionBaseEventServiceIF<T extends BadgeDefinitionGenericEvent> extends CacheAddressableEventServiceIF<T> {
//  @Override
//  Optional<T> getEvent(@NonNull String eventId);
}
