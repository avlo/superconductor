package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeSetsEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeSetsEventServiceIF extends CacheTagMappedEventServiceIF<BadgeSetsEvent, AddressTag>, CacheCuratedEventServiceIF<BadgeSetsEvent> {
  List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag);
  Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag);
  Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag);
  Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
}
