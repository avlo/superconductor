package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;

public interface CacheBadgeAwardReputationEventServiceIF extends CacheTagMappedEventServiceIF<BadgeAwardReputationEvent, AddressTag>, EventMaterializer<BadgeAwardReputationEvent> {
}
