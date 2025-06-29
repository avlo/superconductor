package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindTypeIF;

public interface EventKindTypePluginIF<KindTypeIF> extends EventKindPluginIF<Kind> {
  KindTypeIF getKindType();
  void processIncomingEvent(GenericEventKindTypeIF event);
}
