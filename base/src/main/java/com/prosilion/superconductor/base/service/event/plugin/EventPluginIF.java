package com.prosilion.superconductor.base.service.event.plugin;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;

public interface EventPluginIF {
  GenericEventRecord processIncomingEvent(EventIF event);
}
