package com.prosilion.superconductor.base.cache.mapped;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.TagMappedEventIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * Maps EventTag/AddressTag to an Event
 */
public interface CacheTagMappedEventServiceIF<T extends TagMappedEventIF> {
  T materialize(@NonNull EventIF eventIF) throws NostrException;
  Optional<T> getEvent(@NonNull String eventId, @NonNull String url);
  Kind getKind();
}
