package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.DeletionEventEntityIF;
import com.prosilion.superconductor.base.EventIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;

public interface CacheIF {
  Optional<EventIF> getByEventIdString(@NonNull String eventId);
  Optional<EventIF> getByMatchingAddressableTags(@NonNull String eventId);
  void saveEventEntityOrDocument(@NonNull GenericEventKindIF event);
  void deleteEventEntity(@NonNull EventIF eventIF);
  List<DeletionEventEntityIF> getAllDeletionEventEntities();
  <T> Map<Kind, Map<T, GenericEventKindIF>> getAllEventEntities();

  default <T> Map<Kind, Map<T, GenericEventKindIF>> getAll() {
    return this.<T>getAllEventEntities().entrySet().stream()
        .filter(eventMap ->
            eventMap.getValue().keySet().stream().noneMatch(eventId ->
                checkEventIdMatchesAnyDeletionEventEntityId(
                    eventId,
                    getAllDeletionEventEntities())))
        .filter(kindMapEntry ->
            !kindMapEntry.getKey().equals(Kind.CLIENT_AUTH))
        .collect(
            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  default <T> boolean checkEventIdMatchesAnyDeletionEventEntityId(T eventId, List<DeletionEventEntityIF> deletionEventEntities) {
    return deletionEventEntities.stream().map(DeletionEventEntityIF::getEventId).anyMatch(eventId::equals);
  }
}
