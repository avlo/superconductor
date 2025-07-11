package com.prosilion.superconductor.base.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;

public interface EventKindTypePluginIF<KindTypeIF> extends EventPluginIF {
  Kind getKind();
  KindTypeIF getKindType();
}
