package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheFollowSetsEventServiceIF extends CacheTagMappedEventServiceIF<FollowSetsEvent> {
  @Override
  Optional<FollowSetsEvent> getEvent(@NonNull String eventId);
  List<FollowSetsEvent> getEventsByPubkeyTag(@NonNull PublicKey badgeAwardRecipientPublicKey);
}
