package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeDefinitionAbstractEventServiceIF<T extends AddressableEvent> extends CacheAddressableEventServiceIF<T, AddressTag> {
  Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay);
  Optional<T> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag);
}
