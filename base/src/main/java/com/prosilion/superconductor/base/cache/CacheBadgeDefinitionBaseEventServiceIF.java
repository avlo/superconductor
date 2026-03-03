package com.prosilion.superconductor.base.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import org.checkerframework.checker.nullness.qual.NonNull;

// TODO: future possibly integrate with CacheBadgeDefinitionReputationEventServiceIF
//       requires potential refactor between AddressableEvent and TagMappedEventIF
public interface CacheBadgeDefinitionBaseEventServiceIF<T extends BadgeDefinitionGenericEvent> extends CacheAddressableEventServiceIF<T> {
  @Override
  T materialize(@NonNull EventIF eventIF) throws JsonProcessingException;
}
