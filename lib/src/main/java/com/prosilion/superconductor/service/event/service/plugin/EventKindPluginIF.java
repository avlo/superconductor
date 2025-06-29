package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.superconductor.service.event.type.EventPluginIF;

public interface EventKindPluginIF<Kind> extends EventPluginIF {
  Kind getKind();
}
