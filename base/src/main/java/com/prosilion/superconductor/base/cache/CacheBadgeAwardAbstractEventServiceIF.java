package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import lombok.NonNull;

public interface CacheBadgeAwardAbstractEventServiceIF<
   S extends AddressableEvent,
   T extends BadgeAwardAbstractEvent<S>,
   U extends ReferencedAbstractEventTag> extends CacheTagMappedEventServiceIF<T, U>, EventMaterializer<T> {
  
  Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay);
  Optional<T> materialize(@NonNull EventIF eventIF);
  Kind getKind();
}
