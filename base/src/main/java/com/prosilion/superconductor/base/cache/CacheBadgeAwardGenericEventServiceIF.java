package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import org.springframework.lang.NonNull;

public interface CacheBadgeAwardGenericEventServiceIF<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends CacheTagMappedEventServiceIF<T>, EventMaterializer<BaseEvent> {
  String INVALID_REMOTE_URL = "EventTag [%s] is missing remote url";

  @Override
  T materialize(@NonNull EventIF eventIF);
}
