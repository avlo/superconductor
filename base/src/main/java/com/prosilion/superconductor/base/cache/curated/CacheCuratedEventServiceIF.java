package com.prosilion.superconductor.base.cache.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import lombok.NonNull;

public interface CacheCuratedEventServiceIF<T extends AddressableEvent> extends EventMaterializer<T> {
  Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay);
  Kind getKind();
}
