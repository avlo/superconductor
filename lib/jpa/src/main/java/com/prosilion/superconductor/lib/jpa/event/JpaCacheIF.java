package com.prosilion.superconductor.lib.jpa.event;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.service.event.CacheIF;
import org.springframework.lang.NonNull;

public interface JpaCacheIF extends CacheIF {
  GenericEventKindIF getEventById(@NonNull Long id);
}
