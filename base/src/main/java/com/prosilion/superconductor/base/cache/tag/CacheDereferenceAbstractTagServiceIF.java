package com.prosilion.superconductor.base.cache.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import java.util.Optional;

public interface CacheDereferenceAbstractTagServiceIF<T extends ReferencedAbstractEventTag> {
  Optional<GenericEventRecord> getEvent(T t);
  String getSuperconductorRelayUrl();
}
