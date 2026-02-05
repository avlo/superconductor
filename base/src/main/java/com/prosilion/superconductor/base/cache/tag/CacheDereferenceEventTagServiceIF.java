package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.EventTag;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheDereferenceEventTagServiceIF extends CacheDereferenceAbstractTagServiceIF<EventTag> {
  @Override
  Optional<GenericEventRecord> getEvent(EventTag t);
  default Optional<GenericEventRecord> getEvent(@NonNull String eventId, @NonNull String url) {
    return getEvent(new EventTag(eventId, url));
  }
}
