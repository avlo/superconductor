package com.prosilion.superconductor.autoconfigure.curation.service;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.RelayTag;
import java.util.Optional;
import lombok.NonNull;

public interface CacheCuratedBadgeDefinitionGenericEventServiceIF extends
   CacheCuratedAbstractEventServiceIF<
      CuratedBadgeDefinitionGenericEvent,
      BadgeDefinitionGenericEvent,
      AddressTag> {
  Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull AddressTag addressTag);
  Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull EventTag eventTag);
  Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull AddressTag addressTag, Optional<RelayTag> relayTag, @NonNull Relay fromRelay);
}
