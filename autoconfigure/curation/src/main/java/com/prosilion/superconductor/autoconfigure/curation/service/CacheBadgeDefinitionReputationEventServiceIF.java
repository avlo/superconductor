package com.prosilion.superconductor.autoconfigure.curation.service;

import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.service.event.CacheBadgeDefinitionAbstractEventServiceIF;
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
  Optional<BadgeDefinitionReputationEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
}
