package com.prosilion.superconductor.base.service.event.service.plugin;

import com.prosilion.superconductor.base.service.event.type.EventPluginIF;

public interface EventKindPluginIF<Kind> extends EventPluginIF {
  Kind getKind();
}
