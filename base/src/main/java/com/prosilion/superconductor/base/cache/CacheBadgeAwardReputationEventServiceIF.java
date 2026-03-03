package com.prosilion.superconductor.base.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardReputationEventServiceIF extends CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> {
  @Override
  BadgeAwardReputationEvent materialize(@NonNull EventIF eventIF);
  @Override
  Optional<BadgeDefinitionReputationEvent> getEventTagEvent(@NonNull String eventId, @NonNull String url) throws JsonProcessingException;
}
