package com.prosilion.superconductor.base.service.event.plugin;

import com.prosilion.nostr.event.BaseEvent;

public interface EventPluginIF<T extends BaseEvent> {
  void processIncomingEvent(T event);
}
