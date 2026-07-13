package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import java.util.Optional;
import lombok.NonNull;

/**
 * This interface exists as shorthand convenience for developers via short/easily understandable:
 * CacheBadgeDefinitionGenericEventServiceIF variableName;
 * <p>
 * rather than longer & more complex/error-prone variant:
 * CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent> variableName;
 */
public interface CacheBadgeDefinitionAbstractEventServiceIF<T extends AddressableEvent> extends CacheAddressableEventServiceIF<T, AddressTag> {
  //  Optional<BadgeDefinitionGenericEvent> getBy(AddressTag addressTag);
  Optional<T> getBy(@NonNull AddressTag addressTag);
  Optional<T> getBy(@NonNull AddressTag addressTag, @NonNull PubKeyTag pubKeyTag);
  Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay);
  Kind getKind();
}
