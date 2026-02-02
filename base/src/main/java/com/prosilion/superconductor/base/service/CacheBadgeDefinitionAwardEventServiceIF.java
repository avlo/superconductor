package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.tag.EventTag;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeDefinitionAwardEventServiceIF extends CacheBadgeDefinitionBaseEventServiceIF<BadgeDefinitionAwardEvent> {
  @Override
  Optional<BadgeDefinitionAwardEvent> getEvent(@NonNull String eventId, @NonNull String url);
  Optional<BadgeDefinitionAwardEvent> getEvent(@NonNull EventTag eventTag);
}
