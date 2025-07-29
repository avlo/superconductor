package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface JpaCacheIF extends CacheIF {
  Optional<EventEntityIF> getEventById(@NonNull Long id);

  // impls parent IF (CacheIF) spec...
  Optional<EventEntityIF> getByEventIdString(@NonNull String eventId);

  // impls parent IF (CacheIF) spec...
  default void deleteEventEntity(@NonNull EventIF eventIF) {
    Optional<EventEntityIF> eventByIdStringAsEventEntityIF =
        getByEventIdString(eventIF.getEventId());

    eventByIdStringAsEventEntityIF
        .map(EventEntityIF::getId).ifPresent(
            // ... which in turn calls our own interface variant...   
            this::deleteEventEntity);
  }

  // ... of which is impld by impl-ers (JpaCache)
  void deleteEventEntity(@NonNull Long id);
}
