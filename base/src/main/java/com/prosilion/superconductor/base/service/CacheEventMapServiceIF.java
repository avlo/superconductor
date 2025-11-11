package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

interface CacheEventMapServiceIF<T extends BaseEvent> {
  List<T> getAll();
  List<T> getByKind(Kind kind);
  Optional<T> getEvent(@NonNull EventIF eventIF);
  Optional<T> getEventByUid(Long id);
  Optional<T> getEventByEventId(String eventId);
  T save(EventIF event);
  <U extends EventIF> void deleteEvent(U eventIF);
  List<Long> getAllDeletionEventIds();
  BaseEvent createBaseEventFromEntityIF(@NonNull GenericEventRecord genericEventRecord, @NonNull Class<T> baseEventFromKind);
}
