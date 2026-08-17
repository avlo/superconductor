package com.prosilion.superconductor.autoconfigure.curation.service;

import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public interface CacheCuratedBadgeAwardGenericEventServiceIF extends CacheTagMappedEventServiceIF<CuratedBadgeAwardGenericEvent, EventTag>, CacheCuratedEventServiceIF<CuratedBadgeAwardGenericEvent> {
  List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag);
  List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
  Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag);
  Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag);
  Optional<CuratedBadgeAwardGenericEvent> getByDirect(@NonNull AddressTag addressTag);
}
