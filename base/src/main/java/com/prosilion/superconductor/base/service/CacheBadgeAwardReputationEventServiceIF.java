package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.user.PublicKey;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardReputationEventServiceIF extends CacheBadgeAwardEventServiceIF<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> {
  @Override
  Optional<BadgeAwardReputationEvent> getEvent(@NonNull String eventId, @NonNull String url);
  Optional<BadgeAwardReputationEvent> getEvent(
      @NonNull PublicKey awardReputationRecipientPublicKey,
      @NonNull PublicKey eventCreatorPublicKey
//      , @NonNull IdentifierTag uuid
  );

//  BadgeDefinitionReputationEvent getBadgeDefinitionReputationEvent(@NonNull GenericEventRecord genericEventRecord);
//
//  @Override
//  default BadgeDefinitionReputationEvent getBadgeDefinitionAwardEvent(@NonNull GenericEventRecord genericEventRecord) {
//    return getBadgeDefinitionReputationEvent(genericEventRecord);
//  }
}
