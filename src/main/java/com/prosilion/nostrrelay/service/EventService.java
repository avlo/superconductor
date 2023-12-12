package com.prosilion.nostrrelay.service;

import nostr.event.BaseMessage;

public interface EventService<T extends MessageGeneric> {
  BaseMessage processIncoming(T message);
}
