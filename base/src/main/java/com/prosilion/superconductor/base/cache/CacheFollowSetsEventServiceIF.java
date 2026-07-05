package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheFollowSetsEventServiceIF extends CacheTagMappedEventServiceIF<FollowSetsEvent, EventTag>, EventMaterializer<FollowSetsEvent> {
  @Override
  Optional<FollowSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay);
  @Override
  Optional<FollowSetsEvent> materialize(@NonNull EventIF eventIF);

  @Deprecated
  List<FollowSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag);

  List<BadgeAwardReputationEvent> getBadgeAwardReputationEvents(@NonNull FollowSetsEvent followSetsEvent);
//  Optional<BadgeAwardGenericEventAux> getBy(@NonNull EventTag eventTag);
}
