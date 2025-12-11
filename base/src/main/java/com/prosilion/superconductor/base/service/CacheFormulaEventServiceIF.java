package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.FormulaEvent;
import java.util.Optional;

public interface CacheFormulaEventServiceIF extends CacheTagMappedEventServiceIF<FormulaEvent> {
  @Override
  Optional<FormulaEvent> getEvent(String eventId);
}
