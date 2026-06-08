package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.Optional;
import lombok.NonNull;

public interface CacheFollowSetsEventServiceIF extends CacheTagMappedEventServiceIF<FollowSetsEvent, AddressTag> {
  @Override
  Optional<FollowSetsEvent> getEvent(@NonNull String eventId, @NonNull String url);
  @Override
  FollowSetsEvent materialize(@NonNull EventIF eventIF);

  Optional<BadgeAwardReputationEvent> getBadgeAwardReputationEvent(@NonNull FollowSetsEvent followSetsEvent);
  Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getBy(@NonNull EventTag eventTag);
}
