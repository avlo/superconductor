package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;

public interface EventKindPluginIF {
  void processIncomingEvent(GenericEventKindIF event);
  Kind getKind();
}
