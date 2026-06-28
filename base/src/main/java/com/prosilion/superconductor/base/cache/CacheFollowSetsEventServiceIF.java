package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardGenericEventAux;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import lombok.NonNull;

public interface CacheFollowSetsEventServiceIF extends CacheTagMappedEventServiceIF<FollowSetsEvent, AddressTag>, EventMaterializer<FollowSetsEvent> {
  @Override
  Optional<FollowSetsEvent> getEvent(@NonNull String eventId, @NonNull String url);
  @Override
  Optional<FollowSetsEvent> materialize(@NonNull EventIF eventIF);

  Optional<BadgeAwardReputationEvent> getBadgeAwardReputationEvent(@NonNull FollowSetsEvent followSetsEvent);
  Optional<BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> getBy(@NonNull EventTag eventTag);
}
