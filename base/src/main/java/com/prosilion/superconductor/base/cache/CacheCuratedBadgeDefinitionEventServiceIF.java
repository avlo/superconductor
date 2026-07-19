package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheCuratedBadgeDefinitionEventServiceIF extends CacheTagMappedEventServiceIF<CuratedBadgeDefinitionGenericEvent, EventTag>, EventMaterializer<CuratedBadgeDefinitionGenericEvent> {
  Optional<CuratedBadgeDefinitionGenericEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag);
  Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull EventTag eventTag);
  Optional<CuratedBadgeDefinitionGenericEvent> getBy(@NonNull AddressTag addressTag);
  GenericEventRecord save(EventIF event);
}
