package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;

public interface EventKindTypePluginIF<T extends BaseEvent> extends EventPluginIF<T> {
  Kind getKind();
  KindTypeIF getKindType();
}
