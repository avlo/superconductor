package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.CurationSetsEvent;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheCurationSetsEventServiceIF extends CacheTagMappedEventServiceIF<CurationSetsEvent, EventTag>, EventMaterializer<CurationSetsEvent> {
  List<CurationSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag);
  List<CurationSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
  Optional<CurationSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag);
}
