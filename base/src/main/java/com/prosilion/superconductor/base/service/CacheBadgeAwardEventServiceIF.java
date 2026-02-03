package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardEventServiceIF<U extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<U>> extends CacheTagMappedEventServiceIF<T> {
  @Override
  Optional<T> getEvent(@NonNull String eventId, @NonNull String url);
//  U getBadgeDefinitionGenericEvent(@NonNull GenericEventRecord genericEventRecord);
}
