package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeAwardReputationEventServiceIF extends CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> {

  @Override
  Optional<BadgeAwardReputationEvent> getBy(@NonNull AddressTag addressTag);

  @Override
  BadgeAwardReputationEvent materialize(@NonNull EventIF eventIF);
}
