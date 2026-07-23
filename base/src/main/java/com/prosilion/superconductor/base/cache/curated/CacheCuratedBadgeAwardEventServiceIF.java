package com.prosilion.superconductor.base.cache.curated;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public interface CacheCuratedBadgeAwardEventServiceIF extends CacheTagMappedEventServiceIF<CuratedBadgeAwardGenericEvent, EventTag>, EventMaterializer<CuratedBadgeAwardGenericEvent>, CacheCuratedEventServiceIF<CuratedBadgeAwardGenericEvent> {
  List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag);
  List<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
  Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag);
  Optional<CuratedBadgeAwardGenericEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag);
  CuratedBadgeAwardGenericEvent createFromFetched(@NonNull BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent, @NonNull Relay relay);
}
