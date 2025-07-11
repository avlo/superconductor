package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.entity.EventEntity;
import com.prosilion.superconductor.base.entity.join.deletion.DeletionEventEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheIF {
  Map<Kind, Map<Long, GenericEventKindIF>> getAll();

  Optional<EventEntity> getByEventIdString(@NonNull String eventId);

  Optional<EventEntity> getByMatchingAddressableTags(@NonNull String eventId);

  GenericEventKindIF getEventById(@NonNull Long id);

  void saveEventEntity(@NonNull GenericEventKindIF event);

  void deleteEventEntity(@NonNull EventEntity event);

  default boolean checkEventIdMatchesAnyDeletionEventEntityId(Long eventId, List<DeletionEventEntity> deletionEventEntities) {
    return deletionEventEntities.stream().map(DeletionEventEntity::getEventId).anyMatch(eventId::equals);
  }
}
