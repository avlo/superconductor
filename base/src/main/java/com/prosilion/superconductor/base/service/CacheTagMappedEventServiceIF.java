package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.TagMappedEventIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * Maps EventTag/AddressTag to an Event
 */
public interface CacheTagMappedEventServiceIF<T extends TagMappedEventIF> {
  T reconstruct(@NonNull T event);
  Optional<T> getEvent(@NonNull String eventId);
  Kind getKind();
}
