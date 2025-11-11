package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheFormulaEventServiceIF extends CacheEventMapServiceIF<FormulaEvent> {
  List<FormulaEvent> getAll();
  List<FormulaEvent> getByKind(Kind kind);
  Optional<FormulaEvent> getEvent(@NonNull EventIF eventIF);
  Optional<FormulaEvent> getEventByUid(Long id);
  Optional<FormulaEvent> getEventByEventId(String eventId);
  FormulaEvent save(EventIF event);
  <U extends EventIF> void deleteEvent(U eventIF);
  List<Long> getAllDeletionEventIds();
}
