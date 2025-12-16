package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public class CacheDereferenceEventTagService implements CacheDereferenceEventTagServiceIF {
  CacheServiceIF cacheServiceIF;

  public CacheDereferenceEventTagService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull EventTag eventTag) {
    return cacheServiceIF.getEventByEventId(eventTag.getIdEvent());
  }
}
