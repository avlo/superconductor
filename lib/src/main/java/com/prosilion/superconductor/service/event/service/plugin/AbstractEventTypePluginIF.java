package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindTypeIF;

public interface AbstractEventTypePluginIF<T extends Kind, KindType, V extends GenericEventKindTypeIF> extends EventTypePluginIF<KindType, V, T> {
  KindType getKindType();
}
