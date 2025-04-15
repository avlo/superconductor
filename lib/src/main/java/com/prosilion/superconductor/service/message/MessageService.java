package com.prosilion.superconductor.service.message;

import nostr.event.BaseMessage;

public interface MessageService<T extends BaseMessage> {
  void processIncoming(T reqMessage, String sessionId);

  String getCommand();
}
