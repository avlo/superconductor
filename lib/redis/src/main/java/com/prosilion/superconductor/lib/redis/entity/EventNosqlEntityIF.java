package com.prosilion.superconductor.lib.redis.entity;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.AddressTagEventTagMappableIF;

public interface EventNosqlEntityIF extends EventIF, AddressTagEventTagMappableIF {
  String getEventId();
}
