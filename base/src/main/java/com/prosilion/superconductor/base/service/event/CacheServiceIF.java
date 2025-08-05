package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import java.util.List;
import java.util.Optional;

public interface CacheServiceIF {
  <T> T save(EventIF event);
  List<? extends EventIF> getAll();
  Optional<? extends EventIF> getEventByEventId(String eventId);
  List<? extends EventIF> getByKind(Kind kind);
  void deleteEventEntity(EventIF eventIF);
}
