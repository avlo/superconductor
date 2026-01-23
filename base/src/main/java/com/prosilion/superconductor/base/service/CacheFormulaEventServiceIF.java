package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;

public interface CacheFormulaEventServiceIF extends CacheTagMappedEventServiceIF<FormulaEvent> {
  //  @Override
//  Optional<FormulaEvent> getEvent(@NonNull String eventId);
  BadgeDefinitionAwardEvent getBadgeDefinitionAwardEvent(GenericEventRecord genericEventRecord);
}
