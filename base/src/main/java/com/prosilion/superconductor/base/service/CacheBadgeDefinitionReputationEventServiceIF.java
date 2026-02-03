package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeDefinitionReputationEventServiceIF extends CacheTagMappedEventServiceIF<BadgeDefinitionReputationEvent> {
  @Override
  Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId, @NonNull String url);
  List<BadgeDefinitionReputationEvent> getExistingReputationDefinitionEvents();
//  List<FormulaEvent> getFormulaEvents(GenericEventRecord genericEventRecord);
}
