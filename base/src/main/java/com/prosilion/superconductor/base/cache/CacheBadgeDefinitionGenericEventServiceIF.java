package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;

/**
 * This interface exists as shorthand convenience for developers via short/easily understandable:
 *    CacheBadgeDefinitionGenericEventServiceIF variableName;
 *
 * rather than longer & more complex/error-prone variant:
 *    CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent> variableName;
 */
public interface CacheBadgeDefinitionGenericEventServiceIF extends CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent> {
}
