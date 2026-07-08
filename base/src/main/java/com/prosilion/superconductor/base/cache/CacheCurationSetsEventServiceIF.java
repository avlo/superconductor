package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.CurationSetsEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheCurationSetsEventServiceIF extends CacheTagMappedEventServiceIF<CurationSetsEvent, EventTag>, EventMaterializer<CurationSetsEvent> {
  @Override
  @Deprecated(since = "CacheTagMappedEventServiceIF may/should not require relay for CurationSetsEvent.  move/remove as appropriate")
  Optional<CurationSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay);
  @Override
  Optional<CurationSetsEvent> materialize(@NonNull EventIF eventIF);

  List<CurationSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag);
  Optional<CurationSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag);
  List<CurationSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
}
