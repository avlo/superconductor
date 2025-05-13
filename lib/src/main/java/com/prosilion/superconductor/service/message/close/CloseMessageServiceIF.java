package com.prosilion.superconductor.service.message.close;

import com.prosilion.superconductor.service.message.MessageServiceIF;
import lombok.NonNull;
import nostr.event.message.CloseMessage;

public interface CloseMessageServiceIF<T extends CloseMessage> extends MessageServiceIF<T> {
  void processIncoming(T reqMessage, String sessionId);
  void removeSubscriberBySessionId(String sessionId);
  void removeSubscriberBySubscriberId(@NonNull String subscriberId);
  String getCommand();
  void closeSession(@NonNull String sessionId);
}
