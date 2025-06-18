package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventDtoIF;

public interface EventKindPluginIF<T extends GenericEventDtoIF> {
  void processIncomingEvent(T event);

  Kind getKind();
}
