package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedEventServiceIF;
import lombok.NonNull;

public abstract class CacheCuratedEventService<T extends AddressableEvent> implements CacheCuratedEventServiceIF<T> {
  protected final CacheServiceIF cacheServiceIF;

  public CacheCuratedEventService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  final public GenericEventRecord save(EventIF event) {
    return cacheServiceIF.save(event);
  }
}
