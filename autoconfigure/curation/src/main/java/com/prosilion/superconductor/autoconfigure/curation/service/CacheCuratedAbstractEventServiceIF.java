package com.prosilion.superconductor.autoconfigure.curation.service;

import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;

public interface CacheCuratedAbstractEventServiceIF<
   T extends AbstractSetsEvent,
   U extends BaseEvent,
   V extends ReferencedAbstractEventTag> extends
   CacheTagMappedEventServiceIF<T, V>,
   CacheCuratedEventServiceIF<T>,
   CacheAddressableEventServiceIF<T, V> {
}
