package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.EventTag;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.lang.NonNull;

public interface CacheServiceIF<T, U extends EventIF> {
  T save(EventIF event);
  List<U> getAll();
  Optional<U> getEventByEventId(String eventId);
  List<U> getByKind(Kind kind);
  void deleteEvent(EventIF eventIF);

  default void deleteEventTags(
      @NonNull EventIF eventIF,
      @NonNull Consumer<U> addDeletionEvent) {
    eventIF.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent)
        .map(this::getEventByEventId)
        .flatMap(Optional::stream)
        .filter(deletionCandidate ->
            deletionCandidate.getPublicKey().equals(eventIF.getPublicKey()))
        .forEach(addDeletionEvent);
  }
}
