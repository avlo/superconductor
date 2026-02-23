package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import org.springframework.lang.NonNull;

@FunctionalInterface
public interface EventMaterializer<T extends BaseEvent> {
  T materialize(@NonNull EventIF eventIF);
}
