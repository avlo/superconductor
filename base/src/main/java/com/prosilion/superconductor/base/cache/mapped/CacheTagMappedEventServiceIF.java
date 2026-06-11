package com.prosilion.superconductor.base.cache.mapped;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.TagMappedEventIF;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import lombok.NonNull;

/**
 * Maps EventTag/AddressTag to an Event
 */
public interface CacheTagMappedEventServiceIF<T extends TagMappedEventIF, U extends ReferencedAbstractEventTag> extends EventMaterializer<BaseEvent> {
  Optional<T> getBy(@NonNull U referencedAbstractEventTag);
  Optional<T> getEvent(@NonNull String eventId, @NonNull String url);
  Kind getKind();
}
