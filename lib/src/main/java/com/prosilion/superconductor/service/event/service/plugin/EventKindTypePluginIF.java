package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.superconductor.service.event.type.EventPluginIF;

public interface EventKindTypePluginIF<KindTypeIF> extends EventPluginIF {
  Kind getKind();
  KindTypeIF getKindType();
  void processIncomingEvent(GenericEventKindTypeIF event);
}
