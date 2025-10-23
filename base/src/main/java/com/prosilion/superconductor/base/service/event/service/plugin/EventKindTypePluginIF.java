package com.prosilion.superconductor.base.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.base.service.event.type.KindTypeIF;

public interface EventKindTypePluginIF extends EventPluginIF {
  Kind getKind();
  KindTypeIF getKindType();
}
