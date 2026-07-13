package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.tag.AddressTag;
import java.util.Optional;
import lombok.NonNull;

/**
 * This interface exists as shorthand convenience for developers via short/easily understandable:
 * CacheBadgeDefinitionGenericEventServiceIF variableName;
 * <p>
 * rather than longer & more complex/error-prone variant:
 * CacheAddressableEventServiceIF<BadgeDefinitionGenericEvent> variableName;
 */
public interface CacheBadgeDefinitionGenericEventServiceIF<T extends BadgeDefinitionGenericEvent> extends CacheBadgeDefinitionAbstractEventServiceIF<T> {
  Optional<T> getBy(@NonNull AddressTag addressTag);
}
