package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.CuratedBadgeDefinitionEvent;
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

public interface CacheCuratedBadgeDefinitionEventServiceIF extends CacheTagMappedEventServiceIF<CuratedBadgeDefinitionEvent, EventTag>, EventMaterializer<CuratedBadgeDefinitionEvent> {
  Optional<CuratedBadgeDefinitionEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag);
  Optional<CuratedBadgeDefinitionEvent> getBy(@NonNull EventTag eventTag);
  Optional<CuratedBadgeDefinitionEvent> getBy(@NonNull AddressTag addressTag);
  GenericEventRecord save(EventIF event);
}
