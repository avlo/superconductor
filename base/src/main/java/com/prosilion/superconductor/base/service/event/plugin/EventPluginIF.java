package com.prosilion.superconductor.base.service.event.plugin;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.Optional;
import java.util.function.BiFunction;
import lombok.NonNull;

public interface EventPluginIF {
  Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay relay);

  BiFunction<CacheServiceIF, EventIF, Optional<GenericEventRecord>> eventAlreadyExistsFxn =
     (cacheServiceIF, eventIF) ->
        cacheServiceIF.getEventByEventId(eventIF.getId());
}
