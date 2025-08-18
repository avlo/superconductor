package com.prosilion.superconductor.base.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;

public interface EventKindPluginIF extends EventPluginIF {
  Kind getKind();
}
