package com.prosilion.superconductor.base.cache.curated;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;

public interface CacheCuratedEventServiceIF {
  GenericEventRecord save(EventIF event);
}
