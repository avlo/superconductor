package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import java.util.Optional;

public interface CacheBadgeAwardReputationEventServiceIF extends CacheTagMappedEventServiceIF<BadgeAwardReputationEvent> {
  @Override
  Optional<BadgeAwardReputationEvent> getEvent(String eventId);
}
