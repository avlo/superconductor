package com.prosilion.nostrrelay.service;

import nostr.base.IEvent;
import nostr.event.message.EventMessage;

public interface EventService<T extends EventMessageService<EventMessage>> {
  IEvent processIncoming();
}
