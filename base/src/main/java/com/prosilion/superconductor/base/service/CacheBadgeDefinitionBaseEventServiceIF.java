package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

// TODO: future possibly integrate with CacheBadgeDefinitionReputationEventServiceIF
//       requires potential refactor between AddressableEvent and TagMappedEventIF
public interface CacheBadgeDefinitionBaseEventServiceIF<T extends BadgeDefinitionGenericEvent> extends CacheAddressableEventServiceIF<T> {
//  @Override
//  Optional<T> getEvent(@NonNull String eventId);
}
