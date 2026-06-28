package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeAwardReputationEventServiceIF extends CacheTagMappedEventServiceIF<BadgeAwardReputationEvent, AddressTag>, EventMaterializer<BadgeAwardReputationEvent> {

  @Override
  Optional<BadgeAwardReputationEvent> getBy(@NonNull AddressTag addressTag);

  @Override
  Optional<BadgeAwardReputationEvent> materialize(@NonNull EventIF eventIF);
}
