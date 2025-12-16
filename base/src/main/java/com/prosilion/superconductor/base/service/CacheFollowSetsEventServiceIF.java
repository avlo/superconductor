package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.FollowSetsEvent;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheFollowSetsEventServiceIF extends CacheTagMappedEventServiceIF<FollowSetsEvent> {
  @Override
  Optional<FollowSetsEvent> getEvent(@NonNull String eventId);
}
