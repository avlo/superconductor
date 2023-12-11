package com.prosilion.nostrrelay.service;

import nostr.event.BaseMessage;

public interface MessageCauldron<T extends BaseMessage> {
  T getMessage();
}
