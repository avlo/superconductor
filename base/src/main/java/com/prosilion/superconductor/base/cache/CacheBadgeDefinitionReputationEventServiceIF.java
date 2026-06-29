package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeDefinitionReputationEventServiceIF extends CacheTagMappedEventServiceIF<BadgeDefinitionReputationEvent, AddressTag>, EventMaterializer<BadgeDefinitionReputationEvent> {
  @Override
  Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId, @NonNull Relay relay);
  @Override
  Optional<BadgeDefinitionReputationEvent> materialize(@NonNull EventIF eventIF);
  Optional<BadgeDefinitionReputationEvent> getByDirectTag(@NonNull AddressTag addressTag);
}
