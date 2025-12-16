package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.FormulaEvent;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheFormulaEventServiceIF extends CacheTagMappedEventServiceIF<FormulaEvent> {
  @Override
  Optional<FormulaEvent> getEvent(@NonNull String eventId);
}
