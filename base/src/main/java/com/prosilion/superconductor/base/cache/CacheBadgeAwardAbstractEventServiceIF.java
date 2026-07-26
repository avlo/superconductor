package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;

public interface CacheBadgeAwardAbstractEventServiceIF<
   S extends AddressableEvent,
   T extends BadgeAwardAbstractEvent<S>> extends CacheTagMappedEventServiceIF<T, AddressTag>, EventMaterializer<T> {
}
