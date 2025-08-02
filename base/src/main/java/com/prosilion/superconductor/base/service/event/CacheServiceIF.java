package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheServiceIF {
  <T> T save(EventIF event);
  List<? extends EventIF> getAll();
  Optional<? extends EventIF> getEventByEventId(@NonNull String eventId);
  void deleteEventEntity(@NonNull EventIF eventIF);
  List<? extends EventIF> getByKind(@NonNull Kind kind);
}
