package com.prosilion.nostrrelay.service;

import nostr.base.IEvent;

public interface EventService<T extends EventMessageService> {
  IEvent processIncoming();
}
