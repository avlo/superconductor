package com.prosilion.nostrrelay.service;

import nostr.event.BaseMessage;

public interface EventService {
  BaseMessage processIncoming(BaseMessage message);
}
