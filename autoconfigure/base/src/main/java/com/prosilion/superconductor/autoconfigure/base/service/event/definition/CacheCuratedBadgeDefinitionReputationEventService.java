package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheCuratedFormulaEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public class CacheCuratedBadgeDefinitionReputationEventService implements CacheBadgeDefinitionReputationEventServiceIF {
  private final Identity superconductorInstanceIdentity;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;
  private final CacheCuratedFormulaEventService cacheCuratedFormulaEventService;

  public CacheCuratedBadgeDefinitionReputationEventService(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService,
     @NonNull CacheCuratedFormulaEventService cacheCuratedFormulaEventService) {
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.cacheCuratedFormulaEventService = cacheCuratedFormulaEventService;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getByDirectTag(@NonNull AddressTag addressTag) {
    return Optional.empty();
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return Optional.empty();
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String s, @NonNull Relay relay) {
    return Optional.empty();
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> materialize(@NonNull EventIF eventIF) {
    return Optional.empty();
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getBy(AddressTag addressTag) {
    return Optional.empty();
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return Optional.empty();
  }

  @Override
  public Kind getKind() {
    return null;
  }
}
