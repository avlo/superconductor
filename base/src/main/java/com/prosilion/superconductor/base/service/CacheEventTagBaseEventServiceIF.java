package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.GenericEventRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheEventTagBaseEventServiceIF {
  BaseEvent save(EventIF event);
  List<? extends EventTagsMappedEventsIF> getAll();
  List<? extends EventTagsMappedEventsIF> getByKind(Kind kind);
  Optional<? extends EventTagsMappedEventsIF> getEvent(@NonNull EventIF eventIF);
  Optional<? extends EventTagsMappedEventsIF> getEventByEventId(String eventId);
  <T extends EventIF> void deleteEvent(T event);
//  <T> List<T> getAllDeletionEventIds();
  <T extends BaseEvent> T createBaseEventFromGenericRecord(
      GenericEventRecord genericEventRecord,
      Class<T> baseEventFromKind);
  Kind getKind();
}
