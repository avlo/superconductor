package com.prosilion.superconductor.base.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeDefinitionGenericEventServiceIF extends CacheBadgeDefinitionBaseEventServiceIF<BadgeDefinitionGenericEvent> {
  @Override
  BadgeDefinitionGenericEvent materialize(@NonNull EventIF eventIF) throws JsonProcessingException;
}
