package com.prosilion.superconductor.autoconfigure.curation.service;

import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public interface CacheCuratedBadgeDefinitionGenericEventServiceIF extends CacheTagMappedEventServiceIF<CuratedBadgeDefinitionGenericEvent, EventTag>, CacheCuratedEventServiceIF<CuratedBadgeDefinitionGenericEvent> {
  Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull AddressTag addressTag);
}
