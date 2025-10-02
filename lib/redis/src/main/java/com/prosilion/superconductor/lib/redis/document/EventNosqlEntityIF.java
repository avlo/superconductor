package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.nostr.event.EventIF;

public interface EventNosqlEntityIF extends EventIF {
  String getEventId();
}
