package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;

public interface EventKindPluginIF<T extends GenericEventKindIF> {
  void processIncomingEvent(T event);
  Kind getKind();
}
