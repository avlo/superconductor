package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.event.GenericEventKindIF;

public interface EventKindPluginIF<T> {
  void processIncomingEvent(GenericEventKindIF event);
  T getKind();
}
