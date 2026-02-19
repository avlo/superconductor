package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;

public interface EventKindTypePluginIF extends EventPluginIF {
  Kind getKind();
  KindTypeIF getKindType();
}
