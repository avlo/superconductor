package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindTypeIF;

public interface EventTypePluginIF<KindType, T extends GenericEventKindTypeIF, U extends Kind> {
  void processIncomingEvent(T event);
  U getKind();
}
