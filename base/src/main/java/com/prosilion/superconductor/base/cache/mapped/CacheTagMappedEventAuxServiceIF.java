package com.prosilion.superconductor.base.cache.mapped;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.nostr.tag.SetsPairedEventTagIF;
import java.util.Optional;
import lombok.NonNull;

/**
 * Maps EventTag/AddressTag to an Event
 */
public interface CacheTagMappedEventAuxServiceIF<T extends SetsPairedEventTagIF, U extends ReferencedAbstractEventTag> {
  Optional<T> getEvent(@NonNull String eventId, Relay url);
  Optional<T> materialize(@NonNull EventIF eventIF, Relay relay);
  Kind getKind();
}
