package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.EventTag;
import java.util.Optional;

public interface CacheReferencedEventTagServiceIF extends CacheReferencedAbstractTagServiceIF<EventTag> {
  @Override
  Optional<GenericEventRecord> getEvent(EventTag t);
}
