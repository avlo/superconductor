package com.prosilion.superconductor.base.cache.curated;

import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import java.util.Optional;
import lombok.NonNull;

public interface CacheCuratedEventServiceIF<T extends AddressableEvent> {
  Optional<T> materialize(@NonNull EventIF incomingBadgeSetsEvent);
  GenericEventRecord save(EventIF event);
}
