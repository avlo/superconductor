package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardEventServiceIF<U extends BadgeDefinitionAwardEvent, T extends BadgeAwardGenericEvent<U>> extends CacheTagMappedEventServiceIF<T> {
  @Override
  Optional<T> getEvent(@NonNull String eventId, @NonNull String url);
//  U getBadgeDefinitionAwardEvent(@NonNull GenericEventRecord genericEventRecord);
}
