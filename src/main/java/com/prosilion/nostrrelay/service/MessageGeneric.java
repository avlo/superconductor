package com.prosilion.nostrrelay.service;

import nostr.event.BaseMessage;

public interface MessageGeneric<T extends BaseMessage> {
  T getMessage();
  BaseMessage processIncoming();
}
