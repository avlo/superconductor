package com.prosilion.nostrrelay.service.event;

import nostr.base.IEvent;
import nostr.event.message.EventMessage;

public interface EventService<T extends EventMessage> {
  IEvent processIncoming();
}
