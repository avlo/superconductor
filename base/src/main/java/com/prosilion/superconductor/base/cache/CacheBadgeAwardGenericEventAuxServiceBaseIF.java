package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.SetsPairedEventTagIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventAuxServiceIF;

public interface CacheBadgeAwardGenericEventAuxServiceBaseIF<S extends SetsPairedEventTagIF, T extends SetsPairedEventTagIF> extends CacheTagMappedEventAuxServiceIF<T, AddressTag> {
}
