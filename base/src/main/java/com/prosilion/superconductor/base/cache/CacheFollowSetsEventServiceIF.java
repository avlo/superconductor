package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.List;
import lombok.NonNull;

public interface CacheFollowSetsEventServiceIF extends CacheTagMappedEventServiceIF<FollowSetsEvent, EventTag>, EventMaterializer<FollowSetsEvent> {
  List<BadgeAwardReputationEvent> getBadgeAwardReputationEvents(@NonNull FollowSetsEvent followSetsEvent);
//  Optional<BadgeAwardGenericEventAux> getBy(@NonNull EventTag eventTag);
}
