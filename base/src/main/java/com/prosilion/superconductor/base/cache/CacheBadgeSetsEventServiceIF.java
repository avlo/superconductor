package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeSetsEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeSetsEventServiceIF extends CacheTagMappedEventServiceIF<BadgeSetsEvent, AddressTag>, EventMaterializer<BadgeSetsEvent> {
  @Override
  Optional<BadgeSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay);
  @Override
  Optional<BadgeSetsEvent> materialize(@NonNull EventIF eventIF);

  List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag);
  List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag);
}
