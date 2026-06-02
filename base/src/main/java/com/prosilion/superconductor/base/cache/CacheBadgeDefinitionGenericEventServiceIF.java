package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;

/**
 * This interface exists as shorthand convenience for developers via short/easily understandable:
 * CacheBadgeDefinitionGenericEventServiceIF variableName;
 * <p>
 * rather than longer & more complex/error-prone variant:
 * CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent> variableName;
 */
public interface CacheBadgeDefinitionGenericEventServiceIF extends CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent, AddressTag> {
//  Optional<BadgeDefinitionGenericEvent> getBy(AddressTag addressTag);
}
