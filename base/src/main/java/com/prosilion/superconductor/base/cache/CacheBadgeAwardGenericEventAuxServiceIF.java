package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardGenericEventAux;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.SetsPairedEventTagIF;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeAwardGenericEventAuxServiceIF extends CacheBadgeAwardGenericEventAuxServiceBaseIF<SetsPairedEventTagIF, BadgeAwardGenericEventAux> {

  @Override
  Optional<BadgeAwardGenericEventAux> getEvent(@NonNull String eventId, Relay url);
}
