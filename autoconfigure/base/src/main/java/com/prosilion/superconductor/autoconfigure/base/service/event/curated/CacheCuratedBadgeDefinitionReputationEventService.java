package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceDecorIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeDefinitionReputationEventServiceDecorIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedFormulaEventServiceDecorIF;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public class CacheCuratedBadgeDefinitionReputationEventService extends CacheCuratedEventService<BadgeDefinitionReputationEvent> implements CacheCuratedBadgeDefinitionReputationEventServiceDecorIF {
  private final Identity instanceIdentity;
  private final CacheBadgeDefinitionReputationEventServiceDecorIF cacheBadgeDefinitionReputationEventServiceDecorIF;
  private final CacheCuratedFormulaEventServiceDecorIF cacheCuratedFormulaEventServiceDecorIF;

  public CacheCuratedBadgeDefinitionReputationEventService(
     @NonNull Identity instanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceDecorIF cacheBadgeDefinitionReputationEventServiceDecorIF,
     @NonNull CacheCuratedFormulaEventServiceDecorIF cacheCuratedFormulaEventServiceDecorIF) {
    super(cacheServiceIF);
    this.instanceIdentity = instanceIdentity;
    this.cacheCuratedFormulaEventServiceDecorIF = cacheCuratedFormulaEventServiceDecorIF;
    this.cacheBadgeDefinitionReputationEventServiceDecorIF = cacheBadgeDefinitionReputationEventServiceDecorIF;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> materialize(@NonNull EventIF eventIF) {
    return Optional.empty();
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String s, @NonNull Relay relay) {
    return Optional.empty();
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getByDirect(@NonNull AddressTag addressTag) {
    return Optional.empty();
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getByExpanded(AddressTag addressTag) {
    return Optional.empty();
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return Optional.empty();
  }

  @Override
  public Kind getKind() {
    return Kind.REFERENCED_SET;
  }
}
