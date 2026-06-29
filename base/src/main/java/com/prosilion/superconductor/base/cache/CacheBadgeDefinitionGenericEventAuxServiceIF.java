package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventAuxServiceIF;
import java.util.Optional;

/**
 * This interface exists as shorthand convenience for developers via short/easily understandable:
 * CacheBadgeDefinitionGenericEventServiceIF variableName;
 * <p>
 * rather than longer & more complex/error-prone variant:
 * CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent> variableName;
 */
public interface CacheBadgeDefinitionGenericEventAuxServiceIF extends CacheAddressableEventAuxServiceIF<BadgeDefinitionGenericEventAux, AddressTag> {
  Optional<BadgeDefinitionGenericEventAux> getBy(AddressTag addressTag, Relay relay);
//  Optional<BadgeDefinitionGenericEventAux> getEvent(@NonNull String eventId, Relay relay);
}
