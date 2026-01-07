package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheFormulaEventServiceIF extends CacheTagMappedEventServiceIF<FormulaEvent> {
  @Override
  Optional<FormulaEvent> getEvent(@NonNull String eventId);
  BadgeDefinitionAwardEvent getBadgeDefinitionAwardEvent(GenericEventRecord genericEventRecord);
}
