package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.event.EventIF;

public interface EntityServiceIF<T, U extends EventIF> {
  T save(EventIF event);
//  List<U> getAll();
//  Optional<U> getEventByEventId(String eventId);
//  List<U> getByKind(Kind kind);
//  void deleteEvent(EventIF eventIF);

}
