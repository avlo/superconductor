package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginRxRIF;

public interface EventKindPluginRxRIF<T extends BaseEvent> extends EventPluginRxRIF<T> {
  Kind getKind();
}
