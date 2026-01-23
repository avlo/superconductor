package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheAddressableEventServiceIF<T extends AddressableEvent> {
  T materialize(@NonNull EventIF event);
  Optional<T> getEvent(@NonNull String eventId);
  Kind getKind();
}
