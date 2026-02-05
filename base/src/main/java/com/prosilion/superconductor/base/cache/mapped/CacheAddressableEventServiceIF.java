package com.prosilion.superconductor.base.cache.mapped;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheAddressableEventServiceIF<T extends AddressableEvent> {
  T materialize(@NonNull EventIF eventIF);
  Optional<T> getAddressTagEvent(@NonNull AddressTag addressTag) throws NostrException;
  Kind getKind();
}
