package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.nostr.event.EventIF;

public interface EventDocumentIF extends EventIF {
  String getEventIdString();
}
