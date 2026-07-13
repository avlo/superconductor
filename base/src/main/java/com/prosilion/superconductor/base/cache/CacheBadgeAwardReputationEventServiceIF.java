package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.tag.AddressTag;

public interface CacheBadgeAwardReputationEventServiceIF extends CacheBadgeAwardAbstractEventServiceIF<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent, AddressTag> {
}
