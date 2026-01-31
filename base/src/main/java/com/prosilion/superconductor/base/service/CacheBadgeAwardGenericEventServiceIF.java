package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardGenericEventServiceIF extends CacheBadgeAwardEventServiceIF<BadgeDefinitionAwardEvent, BadgeAwardGenericEvent<BadgeDefinitionAwardEvent>> {
  @Override
  Optional<BadgeAwardGenericEvent<BadgeDefinitionAwardEvent>> getEvent(@NonNull String eventId);
}
