package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface JpaCacheServiceIF extends CacheServiceIF {
  Optional<GenericEventRecord> getEventByUid(Long id);
  Optional<GenericEventRecord> getEvent(@NonNull EventIF eventIF);
  List<Long> getAllDeletionEventIds();
}
