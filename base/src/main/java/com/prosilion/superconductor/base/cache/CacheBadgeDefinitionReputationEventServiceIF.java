package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeDefinitionReputationEventServiceIF extends CacheTagMappedEventServiceIF<BadgeDefinitionReputationEvent, AddressTag> {
  @Override
  Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId, @NonNull String url);
  @Override
  BadgeDefinitionReputationEvent materialize(@NonNull EventIF eventIF);
  List<BadgeDefinitionReputationEvent> getByDirectTag(@NonNull AddressTag addressTag);
}
