package com.prosilion.nostrrelay.service;

import nostr.event.BaseMessage;

public interface EventService<T extends MessageCauldron> {
  BaseMessage processIncoming(T message);
}
