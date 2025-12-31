package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardGenericEventServiceIF extends CacheTagMappedEventServiceIF<BadgeAwardGenericEvent> {
  @Override
  Optional<BadgeAwardGenericEvent> getEvent(@NonNull String eventId);
}
