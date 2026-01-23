package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.AddressableEvent;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeDefinitionEventServiceIF<T extends AddressableEvent> extends CacheAddressableEventServiceIF<T> {
  @Override
  Optional<T> getEvent(@NonNull String eventId);
}
