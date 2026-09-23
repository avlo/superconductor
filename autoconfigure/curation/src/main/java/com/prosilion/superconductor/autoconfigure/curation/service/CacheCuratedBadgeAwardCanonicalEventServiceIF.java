package com.prosilion.superconductor.autoconfigure.curation.service;

import com.prosilion.nostr.event.curated.CuratedBadgeAwardCanonicalEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public interface CacheCuratedBadgeAwardCanonicalEventServiceIF extends
   CacheTagMappedEventServiceIF<CuratedBadgeAwardCanonicalEvent, EventTag>,
   CacheCuratedEventServiceIF<CuratedBadgeAwardCanonicalEvent>,
   CacheAddressableEventServiceIF<CuratedBadgeAwardCanonicalEvent, EventTag> {
  List<CuratedBadgeAwardCanonicalEvent> getBy(@NonNull PubKeyTag pubKeyTag);
  List<CuratedBadgeAwardCanonicalEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
  Optional<CuratedBadgeAwardCanonicalEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag);
  Optional<CuratedBadgeAwardCanonicalEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag);
  Optional<CuratedBadgeAwardCanonicalEvent> getByDirect(@NonNull AddressTag addressTag);
  Optional<CuratedBadgeAwardCanonicalEvent> getByDirect(@NonNull EventTag eventTag);
  Optional<CuratedBadgeAwardCanonicalEvent> getByExpanded(EventTag eventTag);
}
