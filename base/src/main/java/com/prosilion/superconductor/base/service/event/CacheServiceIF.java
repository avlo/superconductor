package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import java.util.List;
import java.util.Optional;

public interface CacheServiceIF<T, U extends EventIF> {
  T save(EventIF event);
  List<U> getAll();
  Optional<U> getEventByEventId(String eventId);
  List<U> getByKind(Kind kind);
  void deleteEvent(EventIF eventIF);
}
