package com.prosilion.superconductor.base.service.event.plugin;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.Optional;
import java.util.function.BiFunction;

public interface EventPluginIF {
  GenericEventRecord processIncomingEvent(EventIF event);

  BiFunction<CacheServiceIF, EventIF, Optional<EventIF>> eventAlreadyExistsFxn =
      (cacheServiceIF, eventIF) ->
          cacheServiceIF.getEventByEventId(eventIF.getId())
              .map(EventIF.class::cast);
}
