package com.prosilion.superconductor.lib.redis.entity;

import com.prosilion.nostr.event.EventIF;

public interface EventNosqlEntityIF extends EventIF {
  String getEventId();
}
