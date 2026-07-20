package com.prosilion.superconductor.base.cache.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import java.util.Optional;
import lombok.NonNull;

public interface CacheCuratedEventServiceIF<T extends AddressableEvent> {
  Optional<T> materialize(@NonNull EventIF incomingBadgeSetsEvent);
  Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay);
  GenericEventRecord save(EventIF event);
  Kind getKind();
}
