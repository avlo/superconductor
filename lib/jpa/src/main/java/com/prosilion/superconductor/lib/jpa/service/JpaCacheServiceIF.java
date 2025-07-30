package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.service.GenericEventKindIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface JpaCacheServiceIF extends CacheServiceIF {
  Optional<EventEntityIF> getEventByUid(@NonNull Long id);

  // impls parent IF (CacheIF) spec...
  Optional<EventEntityIF> getEventByEventId(@NonNull String eventId);
  
  // ... of which is impld by impl-ers (JpaCache)
  void deleteEventEntity(@NonNull Long id);
}
