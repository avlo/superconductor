package com.prosilion.superconductor.service.message;

import nostr.event.BaseMessage;

public interface MessageServiceIF<T extends BaseMessage> {
  void processIncoming(T message, String sessionId);

  String getCommand();
}
