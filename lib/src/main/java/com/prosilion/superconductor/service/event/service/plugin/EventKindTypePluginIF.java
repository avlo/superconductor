package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.service.event.type.EventPluginIF;

public interface EventKindTypePluginIF<KindTypeIF> extends EventPluginIF {
  Kind getKind();
  KindTypeIF getKindType();
}
