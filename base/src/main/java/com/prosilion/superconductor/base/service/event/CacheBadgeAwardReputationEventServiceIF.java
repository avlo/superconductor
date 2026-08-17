package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;

public interface CacheBadgeAwardReputationEventServiceIF extends CacheBadgeAwardAbstractEventServiceIF<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> {
}
