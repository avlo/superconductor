package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;

public interface EventKindPluginIF extends EventPluginIF {
  Kind getKind();
}
