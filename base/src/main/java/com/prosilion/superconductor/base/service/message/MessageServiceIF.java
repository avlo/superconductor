package com.prosilion.superconductor.base.service.message;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.message.BaseMessage;

public interface MessageServiceIF<T extends BaseMessage> {
  void processIncoming(T message, String sessionId);

  Command getCommand();
}
