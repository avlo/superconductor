package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import org.springframework.lang.NonNull;

public interface CacheBadgeDefinitionGenericEventServiceIF extends CacheBadgeDefinitionBaseEventServiceIF<BadgeDefinitionGenericEvent> {
  @Override
  BadgeDefinitionGenericEvent materialize(@NonNull EventIF eventIF);
}
