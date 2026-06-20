package com.prosilion.superconductor.base.service.message;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.BaseMessage;
import lombok.NonNull;

public interface MessageServiceIF<T extends BaseMessage> {
  void processIncoming(@NonNull T message, @NonNull String sessionId, @NonNull Relay relay);
  Command getCommand();
}
