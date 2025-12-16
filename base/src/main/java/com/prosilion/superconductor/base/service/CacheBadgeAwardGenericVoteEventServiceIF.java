package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeAwardGenericVoteEvent;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardGenericVoteEventServiceIF extends CacheTagMappedEventServiceIF<BadgeAwardGenericVoteEvent> {
  @Override
  Optional<BadgeAwardGenericVoteEvent> getEvent(@NonNull String eventId);
}
