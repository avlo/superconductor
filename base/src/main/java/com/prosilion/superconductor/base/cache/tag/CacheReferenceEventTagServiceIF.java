package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheReferenceEventTagServiceIF extends CacheReferenceAbstractTagServiceIF<EventTag> {
  List<GenericEventRecord> getExpandedEvents(List<EventTag> t);

  default Optional<GenericEventRecord> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return getByExpanded(new EventTag(eventId, relay.getUrl()));
  }
}
