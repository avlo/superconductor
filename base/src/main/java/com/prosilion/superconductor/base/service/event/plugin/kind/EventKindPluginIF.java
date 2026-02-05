package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;

public interface EventKindPluginIF<T extends BaseEvent> extends EventPluginIF<T> {
  Kind getKind();
}
