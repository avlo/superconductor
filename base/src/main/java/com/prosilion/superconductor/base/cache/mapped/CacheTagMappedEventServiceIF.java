package com.prosilion.superconductor.base.cache.mapped;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.TagMappedEventIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import org.springframework.lang.NonNull;

/**
 * Maps EventTag/AddressTag to an Event
 */
public interface CacheTagMappedEventServiceIF<T extends TagMappedEventIF> extends EventMaterializer<BaseEvent> {
  Optional<T> getEvent(@NonNull String eventId, @NonNull String url) throws JsonProcessingException;
  Kind getKind();
}
