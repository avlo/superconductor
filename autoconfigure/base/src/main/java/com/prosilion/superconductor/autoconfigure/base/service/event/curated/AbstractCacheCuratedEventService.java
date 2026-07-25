package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedEventServiceIF;
import java.util.Optional;
import lombok.NonNull;

public abstract class AbstractCacheCuratedEventService<T extends AddressableEvent> implements CacheCuratedEventServiceIF<T> {
  
//  TODO: should ultimately be private after rxr
  protected final CacheServiceIF cacheServiceIF;

  public AbstractCacheCuratedEventService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheServiceIF.getEventByEventId(eventId).flatMap(this::materialize);
  }

  @Override
  final public GenericEventRecord save(EventIF event) {
    return cacheServiceIF.save(event);
  }
}
