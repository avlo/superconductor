package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import java.util.List;
import java.util.Optional;

public interface EntityServiceIF<T, U extends EventIF> {
  T save(EventIF event);
  List<U> getAll();
  Optional<U> findByEventIdString(String eventId);
  List<U> getEventsByKind(Kind kind);
}
