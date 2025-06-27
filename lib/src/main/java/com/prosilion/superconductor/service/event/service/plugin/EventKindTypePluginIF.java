package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindType;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;

public interface EventKindTypePluginIF<T, U> {
  void processIncomingEvent(GenericEventKindTypeIF event);
  T getKind();
  U getKindType();
}
