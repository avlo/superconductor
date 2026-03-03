package com.prosilion.superconductor.base.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheFollowSetsEventServiceIF extends CacheTagMappedEventServiceIF<FollowSetsEvent> {
  @Override
  Optional<FollowSetsEvent> getEvent(@NonNull String eventId, @NonNull String url) throws JsonProcessingException;
  @Override
  FollowSetsEvent materialize(@NonNull EventIF eventIF);
  Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getEventTagEvent(@NonNull String eventId, @NonNull String url) throws JsonProcessingException;
}
