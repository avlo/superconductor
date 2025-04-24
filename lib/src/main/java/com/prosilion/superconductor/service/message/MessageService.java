package com.prosilion.superconductor.service.message;

import nostr.event.BaseMessage;

public interface MessageService<T extends BaseMessage> {
  void processIncoming(T message, String sessionId);

  String getCommand();
}
