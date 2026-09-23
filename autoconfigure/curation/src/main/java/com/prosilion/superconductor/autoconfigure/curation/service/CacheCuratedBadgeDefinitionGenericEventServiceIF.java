package com.prosilion.superconductor.autoconfigure.curation.service;

import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.Optional;
import lombok.NonNull;

public interface CacheCuratedBadgeDefinitionGenericEventServiceIF extends
   CacheTagMappedEventServiceIF<CuratedBadgeDefinitionGenericEvent, AddressTag>,
   CacheCuratedEventServiceIF<CuratedBadgeDefinitionGenericEvent>,
   CacheAddressableEventServiceIF<CuratedBadgeDefinitionGenericEvent, AddressTag> {
  Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull AddressTag addressTag);
  Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull EventTag eventTag);
  Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull AddressTag addressTag, Optional<RelayTag> relayTag, @NonNull Relay fromRelay);
}
