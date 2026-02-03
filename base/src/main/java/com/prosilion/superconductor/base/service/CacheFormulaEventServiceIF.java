package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import java.util.List;

public interface CacheFormulaEventServiceIF extends CacheTagMappedEventServiceIF<FormulaEvent> {
  //  @Override
//  Optional<FormulaEvent> getEvent(@NonNull String eventId);
//  BadgeDefinitionGenericEvent getBadgeDefinitionGenericEvent(GenericEventRecord genericEventRecord);
  List<FormulaEvent> getFormulaEventsGivenAssociatedBadgeDefinitionGenericEvent(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent);
}
