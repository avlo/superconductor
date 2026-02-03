package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.tag.EventTag;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardGenericEventServiceIF extends CacheBadgeAwardEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> {
  @Override
  Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getEvent(@NonNull String eventId, @NonNull String url);
  Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getEvent(@NonNull EventTag eventTag);
}
