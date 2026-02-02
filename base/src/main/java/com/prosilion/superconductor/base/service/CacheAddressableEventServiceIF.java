package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheAddressableEventServiceIF<T extends AddressableEvent> {
  T materialize(@NonNull GenericEventRecord genericEventRecord);
  Optional<T> getEvent(@NonNull String eventId, @NonNull String url) throws NostrException;
  Kind getKind();
}
