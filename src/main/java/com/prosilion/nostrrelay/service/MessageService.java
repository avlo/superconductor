package com.prosilion.nostrrelay.service;

import nostr.event.BaseMessage;

public interface MessageService<T extends BaseMessage> {
  T processIncoming();
}
