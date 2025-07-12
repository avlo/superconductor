package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.DeletionEventEntityIF;
import com.prosilion.superconductor.base.EventEntityIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheIF {
  Map<Kind, Map<Long, GenericEventKindIF>> getAll();

  Optional<EventEntityIF> getByEventIdString(@NonNull String eventId);

  Optional<EventEntityIF> getByMatchingAddressableTags(@NonNull String eventId);

  GenericEventKindIF getEventById(@NonNull Long id);

  void saveEventEntity(@NonNull GenericEventKindIF event);

  void deleteEventEntity(@NonNull EventEntityIF event);

  default boolean checkEventIdMatchesAnyDeletionEventEntityId(Long eventId, List<DeletionEventEntityIF> deletionEventEntities) {
    return deletionEventEntities.stream().map(DeletionEventEntityIF::getEventId).anyMatch(eventId::equals);
  }
}
