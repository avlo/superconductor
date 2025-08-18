package com.prosilion.superconductor.base.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;

public interface EventKindTypePluginIF extends EventPluginIF {
  Kind getKind();
  KindTypeIF getKindType();
}
