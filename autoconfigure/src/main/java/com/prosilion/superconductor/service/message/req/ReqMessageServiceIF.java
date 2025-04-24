package com.prosilion.superconductor.service.message.req;

import com.prosilion.superconductor.service.message.MessageService;
import lombok.NonNull;
import nostr.event.message.ReqMessage;

public interface ReqMessageServiceIF<T extends ReqMessage> extends MessageService<T> {
  void processIncoming(@NonNull T eventMessage, @NonNull String sessionId);
}
