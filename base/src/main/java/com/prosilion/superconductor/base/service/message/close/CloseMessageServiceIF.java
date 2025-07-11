package com.prosilion.superconductor.base.service.message.close;

import com.prosilion.nostr.enums.Command;
import com.prosilion.superconductor.base.service.message.MessageServiceIF;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.message.CloseMessage;

public interface CloseMessageServiceIF<T extends CloseMessage> extends MessageServiceIF<T> {
  void processIncoming(T reqMessage, String sessionId);
  void removeSubscriberBySessionId(String sessionId);
  void removeSubscriberBySubscriberId(@NonNull String subscriberId);
  Command getCommand();
  void closeSession(@NonNull String sessionId);
}
