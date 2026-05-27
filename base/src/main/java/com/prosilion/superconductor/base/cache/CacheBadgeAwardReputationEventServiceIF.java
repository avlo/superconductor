package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import org.springframework.lang.NonNull;

public interface CacheBadgeAwardReputationEventServiceIF extends CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> {
  @Override
  BadgeAwardReputationEvent materialize(@NonNull EventIF eventIF);
}
