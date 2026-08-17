package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;

/**
 * This interface exists as shorthand convenience for developers via short/easily understandable:
 * CacheBadgeDefinitionGenericEventServiceIF
 * <p>
 * rather than longer & more complex/error-prone variant:
 * CacheBadgeDefinitionAbstractEventServiceIF<BadgeDefinitionGenericEvent>
 */
public interface CacheBadgeDefinitionGenericEventServiceIF extends CacheBadgeDefinitionAbstractEventServiceIF<BadgeDefinitionGenericEvent> {
}
