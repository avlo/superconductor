package com.prosilion.superconductor.lib.jpa.event;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.service.event.CacheIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface JpaCacheIF extends CacheIF {
  Optional<GenericEventKindIF> getEventById(@NonNull Long id);

  Optional<EventEntityIF> getEventByIdStringAsEventEntityIF(@NonNull String eventId);

  void deleteEventEntity(@NonNull Long id);

  default void deleteEventEntity(@NonNull GenericEventKindIF genericEventKindIF) {
    getEventByIdStringAsEventEntityIF(
        genericEventKindIF.getId())
        .map(EventEntityIF::getId).ifPresent(
            this::deleteEventEntity);
  }
}
