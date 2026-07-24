package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.tag.AddressTag;
import java.util.Optional;
import lombok.NonNull;

/**
 * This interface exists as shorthand convenience for developers via short/easily understandable:
 * CacheBadgeDefinitionReputationEventServiceIF
 * <p>
 * rather than longer & more complex/error-prone variant:
 * CacheBadgeDefinitionAbstractEventServiceIF<BadgeDefinitionReputationEvent>
 */
public interface CacheBadgeDefinitionReputationEventServiceIF extends CacheBadgeDefinitionAbstractEventServiceIF<BadgeDefinitionReputationEvent> {
  Optional<BadgeDefinitionReputationEvent> getByDirect(@NonNull AddressTag addressTag);
}
