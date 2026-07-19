package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.CuratedBadgeAwardEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheCuratedBadgeAwardEventServiceIF extends CacheTagMappedEventServiceIF<CuratedBadgeAwardEvent, EventTag>, EventMaterializer<CuratedBadgeAwardEvent> {
  List<CuratedBadgeAwardEvent> getBy(@NonNull PubKeyTag pubKeyTag);
  List<CuratedBadgeAwardEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
  Optional<CuratedBadgeAwardEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag);
  Optional<CuratedBadgeAwardEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag);
  GenericEventRecord save(EventIF event);
}
