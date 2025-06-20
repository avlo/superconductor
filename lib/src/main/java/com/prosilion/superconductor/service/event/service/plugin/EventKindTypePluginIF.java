package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindType;
import com.prosilion.nostr.event.GenericEventKindTypeIF;

public interface EventKindTypePluginIF<T extends GenericEventKindTypeIF> {
  void processIncomingEvent(T event);
  Kind getKind();
  KindType getKindType();
}
