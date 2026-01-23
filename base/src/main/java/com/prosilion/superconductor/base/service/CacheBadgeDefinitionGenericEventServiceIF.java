package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeDefinitionGenericEventServiceIF extends CacheBadgeDefinitionEventServiceIF<BadgeDefinitionAwardEvent> {
  @Override
  Optional<BadgeDefinitionAwardEvent> getEvent(@NonNull String eventId);
}
